package com.td.dcts.eso.experience.model.request.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.td.dcts.eso.disclosureadapter.model.RestEventChannelBo;


import java.util.List;

@JsonInclude(Include.NON_NULL)
public class GetDisclosureListRequest extends BaseRequestContent {
    private List<Product> productsList;
    private RestEventChannelBo eventChannel;

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }

    public RestEventChannelBo getEventChannel() {
        return eventChannel;
    }

    public void setEventChannel(RestEventChannelBo eventChannel) {
        this.eventChannel = eventChannel;
    }
}
