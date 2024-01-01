package com.lucas.opa.model;

import java.util.List;
import lombok.Data;

@Data
public class OpaRequestBodyModel {

  String user;
  List<String> path;
  String method;
}
