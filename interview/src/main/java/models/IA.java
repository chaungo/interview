package models;

public class IA {
    // change from private to public because we will sort list IA by these fields
    public int m_lessThanFive;
    public int m_moreThanFiveLess10;
    public int m_wayTooLate;
    private String m_iaName;

    public IA() {
        new IA("", 0, 0, 0);
    }

    public IA(String iaName, int lessThanFive, int moreThanFiveLess10, int wayTooLate) {
        m_iaName = iaName;
        m_lessThanFive = lessThanFive;
        m_moreThanFiveLess10 = moreThanFiveLess10;
        m_wayTooLate = wayTooLate;
    }

    public String getIaName() {
        return m_iaName;
    }

    public void setIaName(String iaName) {
        this.m_iaName = iaName;
    }

    public int getLessThanFive() {
        return m_lessThanFive;
    }

    public void setLessThanFive(int lessThanFive) {
        this.m_lessThanFive = lessThanFive;
    }

    public int getMoreThanFiveLess10() {
        return m_moreThanFiveLess10;
    }

    public void setMoreThanFiveLess10(int moreThanFiveLess10) {
        this.m_moreThanFiveLess10 = moreThanFiveLess10;
    }

    public int getWayTooLate() {
        return m_wayTooLate;
    }

    public void setWayTooLate(int wayTooLate) {
        this.m_wayTooLate = wayTooLate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_iaName == null) ? 0 : m_iaName.hashCode());
        result = prime * result + m_lessThanFive;
        result = prime * result + m_moreThanFiveLess10;
        result = prime * result + m_wayTooLate;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IA other = (IA) obj;
        if (m_iaName == null) {
            if (other.m_iaName != null)
                return false;
        } else if (!m_iaName.equals(other.m_iaName))
            return false;
        if (m_lessThanFive != other.m_lessThanFive)
            return false;
        if (m_moreThanFiveLess10 != other.m_moreThanFiveLess10)
            return false;
        if (m_wayTooLate != other.m_wayTooLate)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IA [iaName=" + m_iaName + ", lessThanFive=" + m_lessThanFive + ", moreThanFiveLess10=" + m_moreThanFiveLess10
                + ", wayTooLate=" + m_wayTooLate + "]";
    }

}
