package com.td.dcts.eso.experience.model.request.signature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.td.dcts.eso.experience.model.esignlive.Document;
import com.td.dcts.eso.experience.model.esignlive.DocumentItemToSigner;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ESignLiveDocumentWrapper {
  Document document;
  List<DocumentItemToSigner> interactiveFields;

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  public List<DocumentItemToSigner> getInteractiveFields() {
    return interactiveFields;
  }

  public void setInteractiveFields(List<DocumentItemToSigner> interactiveFields) {
    this.interactiveFields = interactiveFields;
  }
}
