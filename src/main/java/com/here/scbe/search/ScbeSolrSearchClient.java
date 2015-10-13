package com.here.scbe.search;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

/**
 * Created by kellyfj on 10/13/15.
 */
public class ScbeSolrSearchClient {
  private static final int MAX = 100;

  public ScbeSolrSearchClient() throws IOException, SolrServerException {
    //String zkHostString = "52.91.192.13:2181/solr";
    //CloudSolrClient solr = new CloudSolrClient(zkHostString);
    //solr.setDefaultCollection("scbe_public");

    SolrClient solr = new HttpSolrClient("http://52.91.192.13:8983/solr/scbe_public");

    UpdateResponse response;
    int status;


    long start = System.currentTimeMillis();
    for(int i=0; i < MAX; i++) {
      SolrInputDocument document = new SolrInputDocument();
      document.addField("id", i);
      document.addField("name", "Gouda cheese wheel");
      document.addField("price", i+".99");
      response = solr.add(document);
      status = response.getStatus();
      if(status != 0) {
        System.err.println("add() -->" + status);
      }
    }
    long end1 = System.currentTimeMillis();
    System.out.println("Time to add " +(end1-start)/MAX + " msecs");

// Remember to commit your changes!

    response = solr.commit();
    status = response.getStatus();
    System.out.println("commit() -->" + status);
    long end2 = System.currentTimeMillis();
    System.out.println("Time to commit " +(end2-end1) + " msecs");
  }

  public static void main(String[] args) throws IOException, SolrServerException {
    ScbeSolrSearchClient s = new ScbeSolrSearchClient();
  }
}
