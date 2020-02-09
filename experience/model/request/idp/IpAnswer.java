package com.td.dcts.eso.experience.model.request.idp;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "answerTXT",
  "questionNO"
})
public class IpAnswer {

  @JsonProperty("answerTXT")
  private String answerTXT;
  @JsonProperty("questionNO")
  private String questionNO;

  @JsonProperty("answerTXT")
  public String getAnswerTXT() {
    return answerTXT;
  }

  @JsonProperty("answerTXT")
  public void setAnswerTXT(String answerTXT) {
    this.answerTXT = answerTXT;
  }

  @JsonProperty("questionNO")
  public String getQuestionNO() {
    return questionNO;
  }

  @JsonProperty("questionNO")
  public void setQuestionNO(String questionNO) {
    this.questionNO = questionNO;
  }


}
