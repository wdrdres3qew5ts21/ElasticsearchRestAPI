/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntt.siriraj.transform.service;

/**
 *
 * @author s.trakulmaiphol
 */
public interface ElasticServiceInterface {
    
    public Object[] importDataToNewIndex(String indexName);
    
    public void deleteIndex(String indexName);

    public Object addNewFieldAndDocuments(String INDEX);
    
}
