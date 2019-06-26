package com.urlshortner.com.urlshortner.abhinav;


import com.google.common.hash.Hashing;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RequestMapping("rest/url")
@RestController
public class UrlShortnerResource {

    @Autowired
    StringRedisTemplate redisTemplate; //both key and value are string

    @GetMapping("/{id}")
    public String getUrl (@PathVariable String id)
    {
        String url =redisTemplate.opsForValue().get(id);
        System.out.println("URL Retrieved: "+url);

        if (url == null){
            throw new RuntimeException("There is no shorter URL for :"+ id);
        }
        return url;
    }
    @PostMapping
    public String create(@RequestBody String url)
    {
        UrlValidator urlValidator=new UrlValidator(
                new String[]{"http","https"} //It will allow only http and https not ftp urls
        );
        if (urlValidator.isValid(url)) //commons dependency to validate url
        {
            String id=Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString(); //unique key for redis--murmur algo encryption
            System.out.println("URL Id generated:" + id);
            redisTemplate.opsForValue().set(id,url);
            //gonna operate only on the value which is url in this case
            return id;
        }
        throw new RuntimeException("URL Invalid:"+url);
    }
}
