package com.passport.web;

import com.passport.db.dbhelper.DBAccess;
import com.passport.dto.ResultDto;
import com.passport.enums.ResultEnum;
import com.passport.webhandler.MinerHandler;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chain")
public class BlockController {

  @Autowired
  private MinerHandler minerHandler;
  @Autowired
  private DBAccess dbAccess;

  @GetMapping("/mine")
  public ResultDto mine(HttpServletRequest request) throws Exception {
    minerHandler.mining();
    return new ResultDto(ResultEnum.SUCCESS);
  }
}
