/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntt.siriraj.transform.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import ntt.siriraj.transform.config.ElasticConfig;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetAction;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import static org.elasticsearch.client.ml.job.config.RuleScope.parser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author s.trakulmaiphol
 */
@Service
public class ElasticService implements ElasticServiceInterface {

    private String INDEX = "mock";
    
    @Autowired
    private RestHighLevelClient elasticClient;
    
    private JSONParser jSONParser = new JSONParser();

    @PostConstruct
    private void initSeeding() {
        Logger.getLogger(ElasticService.class.getName()).log(Level.INFO, "Initial Seeding JSON To Elastic");
        this.deleteIndex(INDEX);
        this.importDataToNewIndex(INDEX);
    }

    @Override
    public void deleteIndex(String indexName) {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse deleteResponse = elasticClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            System.out.println("--- Delete Exisiting Index!!! ---");
            System.out.println(deleteResponse);
        } catch (IOException ex) {
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object[] importDataToNewIndex(String indexName) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<HashMap[]> assessmentListResponse = restTemplate.getForEntity("https://67e9204c-f6e8-4b4d-bce2-5c652502c957.mock.pstmn.io/siri", HashMap[].class);
        HashMap[] assessmentList = assessmentListResponse.getBody();
        BulkRequest request = new BulkRequest(indexName);
        for (HashMap<String, Object> assessment : assessmentList) {
            HashMap removedDocumentId = assessment;
            String docuemntId = "" + assessment.get("_id");
            removedDocumentId.remove("_id");
            request.add(new IndexRequest().id(docuemntId)
                    .source(removedDocumentId));
        }
        try {
            BulkResponse bulkResponse = elasticClient.bulk(request, RequestOptions.DEFAULT);
            int numberOfSuccessRequest = bulkResponse.getItems().length;
            System.out.println("---  Insert Success :" + numberOfSuccessRequest + " totals ---");
            System.out.println(bulkResponse);
        } catch (IOException ex) {
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return assessmentList;
    }

    @Override
    public Object addNewFieldAndDocuments(String index) {
        JSONParser parser = new JSONParser();
        BulkRequest request = new BulkRequest(index);
        try {
            JSONArray assessmentList = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("D:\\siriraj\\addnewField.json"), "UTF-8"));
            for (Object assessmentJson : assessmentList) {
                JSONObject assessment = (JSONObject) assessmentJson;
                assessment.put("timestamp", new Date());
                assessment.put("timestamp_custom", new Date());
                System.out.println(assessment);
                String docuemntId = "" + assessment.get("_id");
                assessment.remove("_id");
                request.add(new IndexRequest(index).id(docuemntId)
                        .source(assessment, XContentType.JSON)
                );
            }
            BulkResponse bulkResponse = elasticClient.bulk(request, RequestOptions.DEFAULT);
            int numberOfSuccessRequest = bulkResponse.getItems().length;
            System.out.println("---  Insert Success :" + numberOfSuccessRequest + " totals ---");
            System.out.println(bulkResponse);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ElasticService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


}
