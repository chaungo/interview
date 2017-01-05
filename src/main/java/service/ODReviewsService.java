package service;

import models.IA;

import java.util.List;

public interface ODReviewsService {
    List<IA> getODReviews(String iaName, String project);
}
