package pl.codepot.butelkatr.bottling.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.codepot.butelkatr.bottling.model.BottlesOrder;

/**
 * Created by mszarlinski on 2015-08-28.
 */
@RestController
@RequestMapping(value = "/bottle", consumes = "application/vnd.pl.codepot.butelkatr.v1+json", produces = MediaType.APPLICATION_JSON_VALUE)
public class BottlingController {

    @RequestMapping(method = RequestMethod.POST)
    public void foo(@RequestBody BottlesOrder bottlesOrder) {

    }
}
