/*
 * Njiwa Open Source Embedded M2M UICC Remote Subscription Manager
 * 
 * 
 * Copyright (C) 2019 - , Digital Solutions Ltd. - http://www.dsmagic.com
 *
 * Njiwa Dev <dev@njiwa.io>
 * 
 * This program is free software, distributed under the terms of
 * the GNU General Public License.
 */ 

package io.njiwa.sr.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



/** @brief This module is used to track Delivery Reports for SMS sent. Each concatenated SMS message is given a tracker ID,
*  delivery for each part is then tracked so that the upper layer can be informed when all parts have been delivered.
*/
@Entity
@Table(name="sr_dlr_tracker",
indexes = {
        @Index(columnList = "msisdn", name="dlr_tr_idx1"),
        @Index(columnList = "id,msisdn", name="dlr_tr_idx2")
})
@SequenceGenerator(name="sr_dlr_tracker_s", sequenceName = "sr_dlr_tracker_seq", allocationSize = 1)
public class DlrTracker {
   // Track DLR for a message or set of concatenated messages.

   @javax.persistence.Id
   @Column(name = "id", unique = true, nullable = false, updatable = false)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sr_dlr_tracker_s")
   private Long Id;

   @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
   private
   Date dateAdded;

   @Column(nullable = false)
   private
   String msisdn = "";

   @ElementCollection
   @CollectionTable(name = "sr_dlr_tracker_message_parts",
           joinColumns = @JoinColumn(name = "message_id"),
           uniqueConstraints = {
                   @UniqueConstraint(columnNames = {"message_id", "partNo"},
                           name = "message_part_idx")
           },
           indexes = {
                   @Index(columnList = "message_id,partno", name="dlr_m_tr_idx1")
           }
   )
   private
   List<MessagePart> messageParts = new ArrayList<MessagePart>();

   public Long getId() {
       return Id;
   }

   public void setId(Long id) {
       Id = id;
   }

   public Date getDateAdded() {
       return dateAdded;
   }

   public void setDateAdded(Date dateAdded) {
       this.dateAdded = dateAdded;
   }

   public String getMsisdn() {
       return msisdn;
   }

   public void setMsisdn(String msisdn) {
       this.msisdn = msisdn;
   }

   public List<MessagePart> getMessageParts() {
       return messageParts;
   }

   public void setMessageParts(List<MessagePart> messageParts) {
       this.messageParts = messageParts;
   }


   public DlrTracker() {}

   public DlrTracker(String msisdn) {
       setMsisdn(msisdn);
   }

   // Mark message delivered and return true if all messages delivered.
   public boolean markMessagePartDelivered(int partNo) {
       List<MessagePart> l = getMessageParts();

       for (MessagePart m : l)
           if (m.getPartNo() == partNo) {
               m.setDeliveredDate(Calendar.getInstance().getTime());
               break;
           }
       // Count non-delivered
       int n = 0;
       for (MessagePart m : l)
           if (m.getDeliveredDate() == null)
               n++;
       return n == 0;
   }

   private static final int MAX_DAYS = 7;
   public static void clearOldTrackers(EntityManager em) {
       try {
           Calendar c = Calendar.getInstance();
           c.add(Calendar.DATE, -MAX_DAYS);
           Date oldT = c.getTime();
           List<DlrTracker> dlist = em.createQuery("FROM DlrTracker  WHERE dateAdded < :t", DlrTracker.class)
                   .setParameter("t", oldT)
                   .getResultList();
           for (DlrTracker d : dlist)
               em.remove(d);
       } catch (Exception ex) {
           String xs = ex.toString();
       }
   }

   @Embeddable
   public static class MessagePart {
       @Column(nullable = false)
       private
       Integer partNo;

       @Column(insertable = false, columnDefinition = "timestamp not null default current_timestamp")
       private
       Date sendDate = Calendar.getInstance().getTime();

       @Column()
       private
       Date deliveredDate; // Null if not yet delivered

       public Integer getPartNo() {
           return partNo;
       }

       public void setPartNo(Integer partNo) {
           this.partNo = partNo;
       }

       public Date getSendDate() {
           return sendDate;
       }

       public void setSendDate(Date sendDate) {
           this.sendDate = sendDate;
       }

       public Date getDeliveredDate() {
           return deliveredDate;
       }

       public void setDeliveredDate(Date deliveredDate) {
           this.deliveredDate = deliveredDate;
       }
   }
}
