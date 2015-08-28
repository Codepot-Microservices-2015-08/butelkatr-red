package pl.codepot.butelkatr.bottling

import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
import org.mockito.Mockito
import pl.codepot.butelkatr.bottling.controller.BottlingController
import pl.codepot.butelkatr.bottling.service.BottlingService
import spock.lang.Specification

abstract class BaseMockMvcSpec extends Specification {

    private BottlingService bottlingService = Mockito.mock(BottlingService.class);

    def setup() {
        setupMocks()
        RestAssuredMockMvc.standaloneSetup(new BottlingController(bottlingService))
    }

    void setupMocks() {
    }

}
