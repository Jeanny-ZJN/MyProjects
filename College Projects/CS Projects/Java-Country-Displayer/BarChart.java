import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import java.awt.Dimension;

/**
 * This class makes it easy to create multi-series bar charts using
 * the JFreeChart library.
 * 
 * @author Anna Rafferty, modified from JFreeDemos: https://github.com/jfree/jfree-demos
 */
public class BarChart extends ApplicationFrame {
    private DefaultCategoryDataset dataset;
    private String xAxisTitle;
    private String yAxisTitle;
    private String title;
    
    

    /**
    * Create a new BarChart.
    * @param chartTitle  title to display both above the chart and as the title
    *                    for the whole window
    * @param xAxisTitle text to display below the x-axis
    * @param yAxisTitle text to display to the left of the y-axis        
    */
	public BarChart(String chartTitle, String xAxisTitle, String yAxisTitle) {
        super(chartTitle);
		    this.title = chartTitle;
        this.xAxisTitle = xAxisTitle;
        this.yAxisTitle = yAxisTitle;
		    dataset = new DefaultCategoryDataset();
		

	}

	/**
	 * Add a new value to an unnamed (default) series.
	 * All values in the same series have the same color on the bar chart.
	 * 
	 * @param barLabel name to display on the x-axis for this bar
	 * @param value y-value for this bar 
	 */
	public void addValue(String barLabel, double value) {
        dataset.addValue(value, "", barLabel);
    }
    
	/**
	 * Add a new value to the given series.
	 * All values in the same series have the same color on the bar chart.
	 * 
	 * @param barLabel name to display on the x-axis for this bar
	 * @param value y-value for this bar 
	 * @param series name of the series to add this value to. This name
	 *               will appear in the legend.
	 */
	public void addValue(String barLabel, double value, String series) {
        dataset.addValue(value, series, barLabel);
    }

	/**
	 * Helper method to make calls to JFreeChart and create the bar chart
	 * object.
	 * 
	 * @return chart representing the data for this BarChart
	 */
	private JFreeChart createChart() {
		final JFreeChart chart = ChartFactory.createBarChart(title,
				xAxisTitle, // domain axis label
				yAxisTitle, // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
		);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}
    
	/**
	 * Make and display a new window containing all data added
	 * to this chart.
	 */
    public void displayChart() {
		    final JFreeChart chart = createChart();
		    final ChartPanel chartPanel = new ChartPanel(chart);
		//If you wish, you can constrain the chart size
        //Add an import of java.awt.Dimension to make the line below work.
        chartPanel.setPreferredSize(new Dimension(700, 570));
		    setContentPane(chartPanel);
        this.pack();
        UIUtils.centerFrameOnScreen(this);
        this.setVisible(true);
    }

}