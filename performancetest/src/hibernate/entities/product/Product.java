package hibernate.entities.product;


public class Product extends Item
{
    private double unitPrice;
    private Double bulkUnitPrice;

    public double getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public Double getBulkUnitPrice()
    {
        return bulkUnitPrice;
    }

    public void setBulkUnitPrice(Double bulkUnitPrice)
    {
        this.bulkUnitPrice = bulkUnitPrice;
    }
}
