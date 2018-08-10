package hibernate.entities.product;

public class Service extends Item
{
    private double hourlyRate;

    public double getHourlyRate()
    {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate)
    {
        this.hourlyRate = hourlyRate;
    }
}
