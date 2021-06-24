package anticorona.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="injection", url="http://${api.url.injection}:8080")
public interface InjectionService {

    @RequestMapping(method= RequestMethod.GET, path="/injections/checkStatus")
    public boolean checkStatus(@RequestParam Long bookingId);

}