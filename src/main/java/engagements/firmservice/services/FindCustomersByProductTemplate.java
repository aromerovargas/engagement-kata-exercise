package engagements.firmservice.services;

import engagements.firmservice.domain.Customer;
import engagements.firmservice.domain.Firm;

import java.util.List;
import java.util.Map;

public interface FindCustomersByProductTemplate {

    Map<Firm, Map<String, List<Customer>>> findByProductTemplateId(Long productTemplateId);
}
