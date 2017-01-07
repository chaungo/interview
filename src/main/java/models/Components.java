package models;

import java.util.ArrayList;
import java.util.List;

public class Components {
    private String m_iaName;
    private List<String> m_sonarKeys;

    public Components() {
        new Components("", new ArrayList<String>());
    }

    public Components(String iaName, List<String> sonarKeys) {
        m_iaName = iaName;
        m_sonarKeys = sonarKeys;
    }

    public String getIaName() {
        return m_iaName;
    }

    public void setIaName(String iaName) {
        this.m_iaName = iaName;
    }

    public List<String> getSonarKeys() {
        return m_sonarKeys;
    }

    public void setSonarKeys(List<String> sonarKeys) {
        this.m_sonarKeys = sonarKeys;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_iaName == null) ? 0 : m_iaName.hashCode());
        result = prime * result + ((m_sonarKeys == null) ? 0 : m_sonarKeys.hashCode());
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
        Components other = (Components) obj;
        if (m_iaName == null) {
            if (other.m_iaName != null)
                return false;
        } else if (!m_iaName.equals(other.m_iaName))
            return false;
        if (m_sonarKeys == null) {
            if (other.m_sonarKeys != null)
                return false;
        } else if (!m_sonarKeys.equals(other.m_sonarKeys))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Components [iaName=" + m_iaName + ", sonarKeys=" + m_sonarKeys + "]";
    }

}
