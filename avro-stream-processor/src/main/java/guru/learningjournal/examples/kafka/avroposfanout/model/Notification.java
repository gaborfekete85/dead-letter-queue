package guru.learningjournal.examples.kafka.avroposfanout.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Notification {

    @JsonProperty("InvoiceNumber")
    private String InvoiceNumber;
    @JsonProperty("CustomerCardNo")
    private String CustomerCardNo;
    @JsonProperty("TotalAmount")
    private Double TotalAmount;
    @JsonProperty("EarnedLoyaltyPoints")
    private Double EarnedLoyaltyPoints;
}
