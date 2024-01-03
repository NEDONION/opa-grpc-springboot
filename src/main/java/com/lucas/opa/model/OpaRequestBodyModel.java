package com.lucas.opa.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpaRequestBodyModel {

  String user;
  List<String> path;
  String method;
}
