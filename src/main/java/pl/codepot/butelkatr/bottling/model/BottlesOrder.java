package pl.codepot.butelkatr.bottling.model;

/**
 * Created by mszarlinski on 2015-08-28.
 */
public class BottlesOrder {

    private Integer wort = 0;

    public Integer getWort() {
        return wort;
    }

    public void setWort(Integer wort) {
        this.wort = wort;
    }

    @Override
    public String toString() {
        return "BottlesOrder{" +
                "wort=" + wort +
                '}';
    }
}
