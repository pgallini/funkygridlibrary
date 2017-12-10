package com.example.android.funkygridlibrary;

/**
 * Created by Paul Gallini on 11/27/17.
 */

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MyPrintDocumentAdapterTest {
    @Mock
    Context mMockContext;

    @Test
    public void buildIconForEmailTest() {
        // TODO get this to work
    }
//        Candidates candidate_1 = new Candidates();
//        candidate_1.setCandidateName("Sally");
//        candidate_1.setCandidateInitials("SM");
//        candidate_1.setCandidateColor("tomatoe");
//        candidate_1.setxCoordinate(9);
//        candidate_1.setyCoordinate(9);
//
//        // need to mock parseColor
////        android.graphics.Color c=mock(android.graphics.Color.class);
//        Color c=mock(Color.class);
//
//        // replacing c with android.graphics.Color removes the warning - but does not fix the error
//        when(c.parseColor( "tomatoe" )).thenReturn(parseInt("#ef9a9a"));
//        ArrayList<Candidates> currCandidatesList = new ArrayList<>();
//        currCandidatesList.add(candidate_1);
//
////        String appName = "thisIsATest";
////        PrintManager printManager = (PrintManager) mMockContext.getSystemService(Context.PRINT_SERVICE);
////        String jobName = appName + "_Results";
////        // we need to send candidatesList into the print adapter becuase it has the calculated results (and the DB does not)
////        printManager.print(jobName, new MyPrintDocumentAdapter(mMockContext, currCandidatesList),
////                null);
//        String iconBitmapName = MyPrintDocumentAdapter.buildIconForEmail(mMockContext, candidate_1);
//        assertThat(iconBitmapName, is("Sally"));
//    }
}
