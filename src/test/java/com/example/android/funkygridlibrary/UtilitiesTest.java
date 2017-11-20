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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UtilitiesTest {

    @Mock
    Context mMockContext;

    @Test
    public void getPerson_A_One_people() {
        Candidates candidate_A = new Candidates();
        candidate_A.setCandidateName("Sally");
        candidate_A.setxCoordinate(9);
        candidate_A.setyCoordinate(9);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_A);

        Candidates candidate = Utilities.getPerson_A(candidatesList);
        assertThat(candidate.getCandidateName(), is("Sal4ly"));
    }

    @Test
    public void getPerson_A_Two_people() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Beth");
        candidate_1.setxCoordinate(4);
        candidate_1.setyCoordinate(4);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Sonjay");
        candidate_2.setxCoordinate(2);
        candidate_2.setyCoordinate(2);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_1);

        Candidates candidate = Utilities.getPerson_A(candidatesList);
        assertThat(candidate.getCandidateName(), is("Beth"));
    }

    @Test
    public void getPerson_A_Four_people() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Beth");
        candidate_1.setxCoordinate(4);
        candidate_1.setyCoordinate(4);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Sonjay");
        candidate_2.setxCoordinate(2);
        candidate_2.setyCoordinate(2);

        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(6);
        candidate_3.setyCoordinate(4);

        Candidates candidate_4 = new Candidates();
        candidate_4.setCandidateName("Seth");
        candidate_4.setxCoordinate(2);
        candidate_4.setyCoordinate(2);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_3);
        candidatesList.add(candidate_1);
        candidatesList.add(candidate_4);

        Candidates candidate = Utilities.getPerson_A(candidatesList);
        assertThat(candidate.getCandidateName(), is("Lee"));
    }

    @Test
    public void getPerson_B_One_people() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(9);
        candidate_1.setyCoordinate(9);

//        Candidates candidate_4 = new Candidates();
//        candidate_4.setCandidateName("Seth");
//        candidate_4.setxCoordinate(2);
//        candidate_4.setyCoordinate(2);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_1);

        Candidates candidate = Utilities.getPerson_B(candidatesList);
        assertThat(candidate, is(nullValue()));
    }


    @Test
    public void getPerson_B_Two_people() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(9);
        candidate_1.setyCoordinate(9);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(2);
        candidate_2.setyCoordinate(2);


        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(6);
        candidate_3.setyCoordinate(4);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_1);
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_3);


        Candidates candidate = Utilities.getPerson_B(candidatesList);
        assertThat(candidate.getCandidateName(), is("Lee"));
    }


    @Test
    public void getPerson_B_Three_people() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(9);
        candidate_1.setyCoordinate(9);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(2);
        candidate_2.setyCoordinate(2);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_1);
        candidatesList.add(candidate_2);

        Candidates candidate = Utilities.getPerson_B(candidatesList);
        assertThat(candidate.getCandidateName(), is("Seth"));
    }

    @Test
    public void getPerson_B_Three_X_Greater_than_2() {
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
        candidatesList.add(candidate_1);
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_3);

        Candidates candidate = Utilities.getPerson_B(candidatesList);
        assertThat(candidate.getCandidateName(), is("Seth"));
    }

    @Test
    public void getPerson_B_Three_X_Less_than_2_Y_More_than_2() {
        Candidates candidate_1 = new Candidates();
        candidate_1.setCandidateName("Sally");
        candidate_1.setxCoordinate(9);
        candidate_1.setyCoordinate(9);

        Candidates candidate_2 = new Candidates();
        candidate_2.setCandidateName("Seth");
        candidate_2.setxCoordinate(3);
        candidate_2.setyCoordinate(9);

        Candidates candidate_3 = new Candidates();
        candidate_3.setCandidateName("Lee");
        candidate_3.setxCoordinate(5.5);
        candidate_3.setyCoordinate(4);

        ArrayList<Candidates> candidatesList = new ArrayList<Candidates>();
        candidatesList.add(candidate_1);
        candidatesList.add(candidate_2);
        candidatesList.add(candidate_3);

        Candidates candidate = Utilities.getPerson_B(candidatesList);
        assertThat(candidate.getCandidateName(), is("Lee"));
    }
}
