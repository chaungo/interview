package service;

import java.util.Map;
import java.util.Set;

public interface RESTService {
    Map<String, Object> getUserInfo(String username);

    Set<Object> getProjects();

    Set<Object> getGroups();

    Set<Object> getUsersOfGroup(String groupName);

    Set<Object> getUsersOfProject(String projectName);

    Object getProjectURL(Object projectName);

    Set<Object> getRolesOfProject(String projectUrl);

    Set<Object> getActorsOfRole(String roleUrl);
}
