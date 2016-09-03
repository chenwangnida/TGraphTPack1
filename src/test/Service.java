package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Service {

	private final String serviceID;
	// list of inputInstances(individuals), rather than list of input parameter.
	private  double[] qos;

	private final List<String> inputList = new ArrayList<String>();
	// list of outputInstances(individuals), ranther than list of output
	// parameter.
	private final List<String> outputList = new ArrayList<String>();

	public String getServiceID() {
		return this.serviceID;
	}

	public List<String> getInputList() {
		return this.inputList;
	}

	public List<String> getOutputList() {
		return this.outputList;
	}


	public double[] getQos() {
		return qos;
	}

	public void setQos(double[] qos) {
		this.qos = qos;
	}

	public Service(String serviceID) {
		this.serviceID = serviceID;
	}

}
