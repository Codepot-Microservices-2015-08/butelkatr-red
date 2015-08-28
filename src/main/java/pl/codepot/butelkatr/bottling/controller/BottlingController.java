package pl.codepot.butelkatr.bottling.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.codepot.butelkatr.bottling.model.BottlesOrder;
import pl.codepot.butelkatr.bottling.service.BottlingService;

/**
 * Created by mszarlinski on 2015-08-28.
 */
@RestController
@RequestMapping(value = "/bottle", consumes = "application/vnd.pl.codepot.butelkatr.v1+json", produces = MediaType.APPLICATION_JSON_VALUE)
public class BottlingController {

    private static Logger log = LoggerFactory.getLogger(BottlingController.class);

    private final BottlingService bottlingService;

    @Autowired
    public BottlingController(final BottlingService bottlingService) {
        this.bottlingService = bottlingService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void orderBottles(@RequestBody final BottlesOrder bottlesOrder) {
        log.info("####### orderBottles: {}", bottlesOrder);

        bottlingService.orderBottles(bottlesOrder);
    }
}
