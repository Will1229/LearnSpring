package service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ConfigurationProperties
public class ServiceProperties {

    @Min(1)
    private int prizeMultiple = 2;

    @Min(0)
    @Max(100)
    private double winBoundary = 30.0;

    @Min(0)
    @Max(100)
    private double freeBoundary = 10.0;

    @Min(0)
    private int defaultPrize = 0;

    @Min(100)
    @Max(10000)
    private int accountCacheCapacity = 100;

    public int getPrizeMultiple() {
        return prizeMultiple;
    }

    public void setPrizeMultiple(final int prizeMultiple) {
        this.prizeMultiple = prizeMultiple;
    }

    public double getWinBoundary() {
        return winBoundary;
    }

    public void setWinBoundary(final double winBoundary) {
        this.winBoundary = winBoundary;
    }

    public double getFreeBoundary() {
        return freeBoundary;
    }

    public void setFreeBoundary(final double freeBoundary) {
        this.freeBoundary = freeBoundary;
    }

    public int getDefaultPrize() {
        return defaultPrize;
    }

    public void setDefaultPrize(final int defaultPrize) {
        this.defaultPrize = defaultPrize;
    }

    public int getAccountCacheCapacity() {
        return accountCacheCapacity;
    }

    public void setAccountCacheCapacity(final int accountCacheCapacity) {
        this.accountCacheCapacity = accountCacheCapacity;
    }
}