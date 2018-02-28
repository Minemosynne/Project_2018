package amandine.project_2018.Controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import amandine.project_2018.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements ProfileMoviesFragment.OnFragmentInteractionListener, ProfileTVShowsFragment.OnFragmentInteractionListener{

    private Button mMoviesButton;
    private Button mTVShowsButton;
    private Button mReviewsButton;
    private Button mStatsButton;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment = null;
            switch(view.getId()){
                case R.id.profileMoviesButton :
                    fragment = new ProfileMoviesFragment();
                    break;
                case R.id.profileTVShowsButton :
                    fragment = new ProfileTVShowsFragment();
                    break;
                case R.id.profileReviewsButton :
                    //TODO
                    break;
                case R.id.profileStatsButton :
                    //TODO
                    break;
                default :
                    fragment = new ProfileMoviesFragment();
                    break;
            }
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.profileFragmentContainer, fragment).commit();
        }
    };

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mMoviesButton = view.findViewById(R.id.profileMoviesButton);
        mTVShowsButton = view.findViewById(R.id.profileTVShowsButton);
        mReviewsButton = view.findViewById(R.id.profileReviewsButton);
        mStatsButton = view.findViewById(R.id.profileStatsButton);

        mMoviesButton.setOnClickListener(mOnClickListener);
        mTVShowsButton.setOnClickListener(mOnClickListener);
        mReviewsButton.setOnClickListener(mOnClickListener);
        mStatsButton.setOnClickListener(mOnClickListener);
        // Inflate the layout for this fragment
        Fragment moviesFragment = new ProfileMoviesFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.profileFragmentContainer, moviesFragment).commit();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onFragmentInteraction(){

    }

}
