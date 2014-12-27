package ui;

import geoJSON.FeatureCollection;

import javax.swing.*;
import javax.swing.table.*;

import mapCandidates.*;

import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class PanelStats extends JPanel {
	
	JLabel lblNewLabel_1 = new JLabel();
	JLabel label_1 = new JLabel();
	JLabel label_3 = new JLabel();
	JLabel label_5 = new JLabel();
	JLabel label_7 = new JLabel();
	
	JLabel label_2 = new JLabel();


	public void getStats() {
		try {
		if( featureCollection == null || featureCollection.ecology == null || featureCollection.ecology.population.size() < 1) {
			System.out.println("no ecology attached "+featureCollection);
			return;
		}
		DistrictMap dm = featureCollection.ecology.population.get(0);
		dm.calcFairnessScores();
		double conversion_to_bits = 1.0/Math.log(2.0);
		DecimalFormat decimal = new DecimalFormat("###,##0.000000000");
		DecimalFormat integer = new DecimalFormat("###,###,###,###,##0");
		//        fairnessScores = new double[]{length,disproportional_representation,population_imbalance,disconnected_pops,power_fairness}; //exponentiate because each bit represents twice as many people disenfranched

		lblNewLabel_1.setText(""+dm.fairnessScores[0]);
		label_1.setText(""+integer.format(dm.fairnessScores[3]));
		label_3.setText(""+decimal.format(dm.fairnessScores[2]*conversion_to_bits)+" bits");
		label_5.setText(""+decimal.format(dm.fairnessScores[1]*conversion_to_bits)+" bits");
		label_7.setText(""+decimal.format(dm.fairnessScores[4]*conversion_to_bits)+" bits");
		label_2.setText(""+decimal.format(Settings.mutation_boundary_rate)+" pct");		
		
		Ecology.history.add(new double[]{
				featureCollection.ecology.generation,
				featureCollection.ecology.population.size(),
				Settings.num_districts,
				Settings.mutation_boundary_rate,
				Settings.geometry_weight,
				Settings.disconnected_population_weight,
				Settings.population_balance_weight,
				Settings.disenfranchise_weight,
				Settings.voting_power_balance_weight,
				dm.fairnessScores[3],
				dm.fairnessScores[2]*conversion_to_bits,
				dm.fairnessScores[1]*conversion_to_bits,
				dm.fairnessScores[4]*conversion_to_bits
		});
		
		try {
			
			String[] dcolumns = new String[4+Candidate.candidates.size()*2];
			String[][] ddata = new String[dm.districts.size()][];
			dcolumns[0] = "District";
			dcolumns[1] = "Population";
			dcolumns[2] = "Winner";
			dcolumns[3] = "Self-entropy";
			
			String[] ccolumns = new String[]{"Party","Delegates","Pop. vote","% del","% pop vote"};
			String[][] cdata = new String[Candidate.candidates.size()][];
			
			double[] elec_counts = new double[Candidate.candidates.size()];
			double[] vote_counts = new double[Candidate.candidates.size()];
			double tot_votes = 0;
			for( int i = 0; i < Candidate.candidates.size(); i++) {
				elec_counts[i] = 0;
				vote_counts[i] = 0;
				dcolumns[i+4] = ""+i+" %";
				dcolumns[i+4+Candidate.candidates.size()] = ""+i+" votes";
			}
			for( int i = 0; i < dm.districts.size(); i++) {
				ddata[i] = new String[dcolumns.length];
				District d = dm.districts.get(i);
				//String population = ""+(int)d.getPopulation();
				double self_entropy = d.getSelfEntropy(null);
				//String edge_length = ""+d.getEdgeLength();
				double [] dd = d.getVotes();
				double total = 0;
				double max = -1;
				int iwinner  = -1;
				for( int j = 0; j < dd.length; j++) {
					vote_counts[j] += dd[j];
					if( dd[j] > max) {
						max = dd[j];
						iwinner = j;
					}
					total += dd[j];
					tot_votes += dd[j];
				}
				if( elec_counts != null && iwinner >= 0) {
					elec_counts[iwinner]++;
				}
				String winner = ""+iwinner;
				ddata[i][0] = ""+i;
				ddata[i][1] = integer.format(d.getPopulation());
				ddata[i][2] = ""+winner;
				ddata[i][3] = ""+decimal.format(self_entropy*conversion_to_bits)+" bits";
				for( int j = 4; j < ddata[i].length; j++) {
					ddata[i][j] = "";
				}
				for( int j = 0; j < dd.length; j++) {
					ddata[i][j+4] = ""+(dd[j]/total);
				}
				for( int j = 0; j < dd.length; j++) {
					ddata[i][j+4+Candidate.candidates.size()] = ""+integer.format(dd[j]);
				}	
			}
			//			String[] ccolumns = new String[]{"Party","Delegates","Pop. vote","% del","% pop vote"};

			for( int i = 0; i < Candidate.candidates.size(); i++) {
				cdata[i] = new String[]{
						""+i,
						""+integer.format(elec_counts[i]),
						""+integer.format(vote_counts[i]),
						""+(elec_counts[i]/(double)dm.districts.size()),
						""+(vote_counts[i]/tot_votes)
				};
			}

			
			TableModel tm = new DefaultTableModel(ddata,dcolumns);
			table.setModel(tm);
			TableModel tm2 = new DefaultTableModel(cdata,ccolumns);
			table_1.setModel(tm2);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		this.invalidate();
		this.repaint();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public PanelStats() {
		this.setLayout(null);
		this.setSize(new Dimension(449, 510));
		this.setPreferredSize(new Dimension(449, 510));
		
		JLabel lblNewLabel = new JLabel("Total edge length:");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(6, 6, 202, 16);
		add(lblNewLabel);
		
		lblNewLabel_1.setBounds(220, 5, 196, 16);
		add(lblNewLabel_1);
		
		JLabel lblDisconnectedPopulation = new JLabel("Disconnected population:");
		lblDisconnectedPopulation.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblDisconnectedPopulation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDisconnectedPopulation.setBounds(6, 34, 202, 16);
		add(lblDisconnectedPopulation);
		
		label_1.setBounds(220, 34, 196, 16);
		add(label_1);
		
		JLabel lblPopulationBalance = new JLabel("Population imbalance:");
		lblPopulationBalance.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblPopulationBalance.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPopulationBalance.setBounds(6, 62, 202, 16);
		add(lblPopulationBalance);
		
		label_3.setBounds(220, 62, 196, 16);
		add(label_3);
		
		JLabel lblDisproportionateRepresentation = new JLabel("Representation imbalance:");
		lblDisproportionateRepresentation.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblDisproportionateRepresentation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDisproportionateRepresentation.setBounds(6, 90, 202, 16);
		add(lblDisproportionateRepresentation);
		
		label_5.setBounds(220, 90, 196, 16);
		add(label_5);
		
		JLabel lblPowerImbalance = new JLabel("Power imbalance:");
		lblPowerImbalance.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblPowerImbalance.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPowerImbalance.setBounds(6, 118, 202, 16);
		add(lblPowerImbalance);
		
		label_7.setBounds(220, 118, 202, 16);
		add(label_7);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(26, 194, 390, 223);
		add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(26, 426, 390, 72);
		add(scrollPane_1);
		
		table_1 = new JTable();
		scrollPane_1.setViewportView(table_1);
		
		JLabel lblBorderMutationRate = new JLabel("Border mutation rate:");
		lblBorderMutationRate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBorderMutationRate.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblBorderMutationRate.setBounds(6, 146, 202, 16);
		add(lblBorderMutationRate);
		
		label_2.setBounds(220, 146, 202, 16);
		add(label_2);
	}
	public FeatureCollection featureCollection;
	private JTable table;
	private JTable table_1;
}