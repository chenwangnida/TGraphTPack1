package test.t1;

import org.jgraph.graph.DefaultEdge;

public class ServiceEdge extends DefaultEdge {

	// average matching type value from source vertice to target vertice
	double avgmt;

	// average semantic distance value value from source vertice to target
	// vertice
	double avgsdt;

	public ServiceEdge(double avgmt, double avgsdt) {
		super();
		this.avgmt = avgmt;
		this.avgsdt = avgsdt;
	}

	public double getAvgmt() {
		return avgmt;
	}

	public void setAvgmt(double avgmt) {
		this.avgmt = avgmt;
	}

	public double getAvgsdt() {
		return avgsdt;
	}

	public void setAvgsdt(double avgsdt) {
		this.avgsdt = avgsdt;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
