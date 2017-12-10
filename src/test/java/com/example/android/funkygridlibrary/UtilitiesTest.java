package com.example.android.funkygridlibrary;

/**
 * Created by Paul Gallini on 11/8/17.
 */

import android.content.Context;

import com.example.android.funkygridlibrary.common.Utilities;
import com.example.android.funkygridlibrary.nineBoxCandidates.Candidates;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UtilitiesTest {

    @Mock
    Context mMockContext;

    @Test
    public void getRecommendations_3_people_test() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(9);
        candidate_1.setyCoordinate(9);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(5.5);
        candidate_2.setyCoordinate(7);

        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(6);
        candidate_3.setyCoordinate(4);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_3);
        candidatesList.add(candidate_1);

        ArrayList<String> candidateList = Utilities.getRecommendations(candidatesList);
        assertThat(candidateList.get(0), is("Sally"));
        assertThat(candidateList.size(), is(1));
    }

    @Test
    public void getRecommendations_5_people_test() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(3.5);
        candidate_1.setyCoordinate(9.2);    // DQ'd

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(5.5);
        candidate_2.setyCoordinate(7.9);    // 13.4

        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(6.9);
        candidate_3.setyCoordinate(4.8);    // 11.7

        Candidates candidate_4 = new Candidates();
        candidate_4.setCandidateName("Marie");
        candidate_4.setxCoordinate(5);
        candidate_4.setyCoordinate(4.9);     // 9.9

        Candidates candidate_5 = new Candidates();
        candidate_5.setCandidateName("Lee");
        candidate_5.setxCoordinate(5.5);
        candidate_5.setyCoordinate(5.5);    //11

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_5);
        candidatesList.add(candidate_3);
        candidatesList.add(candidate_4);
        candidatesList.add(candidate_1);

        ArrayList<String> candidateList = Utilities.getRecommendations(candidatesList);
        assertThat(candidateList.get(0), is("Seth"));
        assertThat(candidateList.get(1), is("Lee"));
        assertThat(candidateList.size(), is(2));
    }

    @Test
    public void getRecommendations_5_all_too_low_test() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(3.5);
        candidate_1.setyCoordinate(9.2);    // DQ'd

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(5.5);
        candidate_2.setyCoordinate(2.9);    // DQ'd

        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(6.9);
        candidate_3.setyCoordinate(3.8);    // DQ'd

        Candidates candidate_4 = new Candidates();
        candidate_4.setCandidateName("Marie");
        candidate_4.setxCoordinate(0);
        candidate_4.setyCoordinate(4.9);    // DQ'd

        Candidates candidate_5 = new Candidates();
        candidate_5.setCandidateName("Lee");
        candidate_5.setxCoordinate(1.5);
        candidate_5.setyCoordinate(1.5);     // DQ'd

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_5);
        candidatesList.add(candidate_3);
        candidatesList.add(candidate_4);
        candidatesList.add(candidate_1);

        ArrayList<String> candidateList = Utilities.getRecommendations(candidatesList);
        assertThat(candidateList.size(), is(0));
    }
}
