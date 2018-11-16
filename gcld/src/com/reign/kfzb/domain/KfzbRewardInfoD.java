package com.reign.kfzb.domain;

import com.reign.framework.hibernate.model.*;

public class KfzbRewardInfoD implements IModel
{
    int pk;
    int day1BaseTicket;
    int day1RoundTicketAdd;
    int layer1Ticket;
    int layer2Ticket;
    int layer3Ticket;
    int layer4Ticket;
    int supportTicket;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getDay1BaseTicket() {
        return this.day1BaseTicket;
    }
    
    public void setDay1BaseTicket(final int day1BaseTicket) {
        this.day1BaseTicket = day1BaseTicket;
    }
    
    public int getDay1RoundTicketAdd() {
        return this.day1RoundTicketAdd;
    }
    
    public void setDay1RoundTicketAdd(final int day1RoundTicketAdd) {
        this.day1RoundTicketAdd = day1RoundTicketAdd;
    }
    
    public int getSupportTicket() {
        return this.supportTicket;
    }
    
    public void setSupportTicket(final int supportTicket) {
        this.supportTicket = supportTicket;
    }
    
    public int getLayer1Ticket() {
        return this.layer1Ticket;
    }
    
    public void setLayer1Ticket(final int layer1Ticket) {
        this.layer1Ticket = layer1Ticket;
    }
    
    public int getLayer2Ticket() {
        return this.layer2Ticket;
    }
    
    public void setLayer2Ticket(final int layer2Ticket) {
        this.layer2Ticket = layer2Ticket;
    }
    
    public int getLayer3Ticket() {
        return this.layer3Ticket;
    }
    
    public void setLayer3Ticket(final int layer3Ticket) {
        this.layer3Ticket = layer3Ticket;
    }
    
    public int getLayer4Ticket() {
        return this.layer4Ticket;
    }
    
    public void setLayer4Ticket(final int layer4Ticket) {
        this.layer4Ticket = layer4Ticket;
    }
}
