package com.here.scbe.search;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
    //String zkHostString = "52.91.192.13:2181,52.91.192.13:2182,52.91.192.13:2183";
    //CloudSolrClient solr = new CloudSolrClient(zkHostString);
    //solr.setDefaultCollection("scbe_public_zk");

    SolrClient solr = new HttpSolrClient("http://52.91.192.13:8983/solr/scbe_public");

    UpdateResponse response;
    int status;

    DescriptiveStatistics addTimeStats = new DescriptiveStatistics();
    DescriptiveStatistics commitTimeStats = new DescriptiveStatistics();

    for(int iter =1;; iter++) {
      long start = System.currentTimeMillis();
      System.out.println("Iteration "+iter);
      for (long i = start; i < start + MAX; i++) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", i);
        document.addField("name", "Gouda cheese wheel " + System.currentTimeMillis());
        document.addField("price", i + ".99");
        long addStart = System.currentTimeMillis();
        response = solr.add(document);
        long addEnd = System.currentTimeMillis();
        addTimeStats.addValue(addEnd - addStart);
        status = response.getStatus();
        if (status != 0) {
          System.err.println("\t add() -->" + status);
        }

      }

      //System.out.println("\t Time to add " + (end1 - start) / MAX + " msecs");
      // Remember to commit your changes!
      long commitStart = System.currentTimeMillis();
      response = solr.commit();
      long commitEnd = System.currentTimeMillis();
      commitTimeStats.addValue(commitEnd - commitStart);
      status = response.getStatus();
      if (status != 0) {
        System.out.println("\t commit() -->" + status);
      }
      //long end2 = System.currentTimeMillis();
     // System.out.println("\t Time to commit " + (end2 - end1) + " msecs");

      if(iter % 1 ==0 ) {
        StringBuilder sb = new StringBuilder();


        sb.append("Add:    Mean = " + String.format("%.1f", addTimeStats.getMean()));
        sb.append(" +/- " + String.format("%.2f", addTimeStats.getStandardDeviation()));
        sb.append("\t Min = " + String.format("%d", (long) addTimeStats.getMin()));
        sb.append(" Max = " + String.format("%d", (long) addTimeStats.getMax()));
        sb.append("\t 99% = " + String.format("%.1f", addTimeStats.getPercentile(99.0)));
        sb.append(" N=" + addTimeStats.getN());
        sb.append("\n");
        sb.append("Commit: Mean = " + String.format("%.1f", commitTimeStats.getMean()));
        sb.append(" +/- " + String.format("%.2f", commitTimeStats.getStandardDeviation()));
        sb.append("\t Min = " + String.format("%d", (long) commitTimeStats.getMin()));
        sb.append(" Max = " + String.format("%d", (long) commitTimeStats.getMax()));
        sb.append("\t 99% = " + String.format("%.1f", commitTimeStats.getPercentile(99.0)));
        sb.append(" N=" + commitTimeStats.getN());

        System.out.println(sb.toString());
      }
    }


  }

  public static void main(String[] args) throws IOException, SolrServerException {
    ScbeSolrSearchClient s = new ScbeSolrSearchClient();
  }
}
