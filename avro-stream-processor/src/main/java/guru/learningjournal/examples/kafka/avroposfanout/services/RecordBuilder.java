package guru.learningjournal.examples.kafka.avroposfanout.services;

import guru.learningjournal.examples.kafka.avroposfanout.model.HadoopRecord;
import guru.learningjournal.examples.kafka.avroposfanout.model.Notification;
import guru.learningjournal.examples.kafka.model.LineItem;
import guru.learningjournal.examples.kafka.model.PosInvoice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecordBuilder {

    public Notification getNotification(PosInvoice invoice){
        return Notification.builder()
                .InvoiceNumber(invoice.getInvoiceNumber().toString())
                .CustomerCardNo(invoice.getCustomerCardNo().toString())
                .TotalAmount(invoice.getTotalAmount())
                .EarnedLoyaltyPoints(invoice.getTotalAmount() * 0.02)
                .build();

//        Notification notification = new Notification();
//        notification.setInvoiceNumber(invoice.getInvoiceNumber());
//        notification.setCustomerCardNo(invoice.getCustomerCardNo());
//        notification.setTotalAmount(invoice.getTotalAmount());
//        notification.setEarnedLoyaltyPoints(invoice.getTotalAmount() * 0.02);
//        if(Double.compare(notification.getEarnedLoyaltyPoints(), 100D) > 0) {
//            throw new RuntimeException("Loyalty point is too low. ");
//        }
//        return notification;
    }

    public PosInvoice getMaskedInvoice(PosInvoice invoice){
        invoice.setCustomerCardNo(null);
        if ("HOME-DELIVERY".equalsIgnoreCase(invoice.getDeliveryType().toString())) {
            invoice.getDeliveryAddress().setAddressLine(null);
            invoice.getDeliveryAddress().setContactNumber(null);
        }
        return invoice;
    }

    public List<HadoopRecord> getHadoopRecords(PosInvoice invoice){
        List<HadoopRecord> records = new ArrayList<>();

        for (LineItem i : invoice.getInvoiceLineItems()) {
            HadoopRecord record = new HadoopRecord();
            record.setInvoiceNumber(invoice.getInvoiceNumber().toString());
            record.setCreatedTime(invoice.getCreatedTime());
            record.setStoreID(invoice.getStoreID().toString());
            record.setPosID(invoice.getPosID().toString());
            record.setCustomerType(invoice.getCustomerType().toString());
            record.setPaymentMethod(invoice.getPaymentMethod().toString());
            record.setDeliveryType(invoice.getDeliveryType().toString());
            record.setItemCode(i.getItemCode().toString());
            record.setItemDescription(i.getItemDescription().toString());
            record.setItemPrice(i.getItemPrice());
            record.setItemQty(i.getItemQty());
            record.setTotalValue(i.getTotalValue());
            if (invoice.getDeliveryType().toString().equalsIgnoreCase("HOME-DELIVERY")) {
                record.setCity(invoice.getDeliveryAddress().getCity().toString());
                record.setState(invoice.getDeliveryAddress().getState().toString());
                record.setPinCode(invoice.getDeliveryAddress().getPinCode().toString());
            }
            records.add(record);
        }
        return records;
    }
}
