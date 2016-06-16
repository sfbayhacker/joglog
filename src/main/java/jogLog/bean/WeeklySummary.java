package jogLog.bean;

/**
 *
 * @author Kish
 */
public class WeeklySummary {
    private String weekStartDate;
    private String totalDistance;
    private String averageSpeed;

    /**
     * @return the weekStartDate
     */
    public String getWeekStartDate() {
        return weekStartDate;
    }

    /**
     * @param weekStartDate the weekStartDate to set
     */
    public void setWeekStartDate(String weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    /**
     * @return the totalDistance
     */
    public String getTotalDistance() {
        return totalDistance;
    }

    /**
     * @param totalDistance the totalDistance to set
     */
    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * @return the averageSpeed
     */
    public String getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * @param averageSpeed the averageSpeed to set
     */
    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
    
    @Override
    public String toString() {
        return "{"+weekStartDate+","+totalDistance+","+averageSpeed+"}";
    }
}
