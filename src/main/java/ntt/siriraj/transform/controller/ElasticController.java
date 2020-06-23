/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntt.siriraj.transform.controller;

import ntt.siriraj.transform.service.ElasticService;
import ntt.siriraj.transform.service.ElasticServiceInterface;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author s.trakulmaiphol
 */
@RestController
public class ElasticController {
    
    private String INDEX = "mock";
    
    private ElasticServiceInterface elasticService;
    
    public ElasticController(ElasticService elasticService){
        this.elasticService = elasticService;
    }
    
    @GetMapping("/import/seeding")
    public Object importData(){
        return elasticService.importDataToNewIndex(INDEX);
    }
    
    
    @GetMapping("/additionalDocuments")
    public Object addNewFieldAndDocuments(){
        return elasticService.addNewFieldAndDocuments(INDEX);
    }
}
