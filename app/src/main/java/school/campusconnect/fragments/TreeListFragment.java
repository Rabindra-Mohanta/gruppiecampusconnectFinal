
package school.campusconnect.fragments;

public class TreeListFragment{}/* extends BaseFragment implements LeadAdapter.OnLeadSelectListener, LeafManager.OnCommunicationListener {

    private static final String TAG = "TreeListFragment";
    private LayoutListWithoutRefreshBinding mBinding;
    private LeadAdapter mAdapter;
    final int REQUEST_CALL = 234;
    final int REQUEST_SMS = 235;
    Intent intent;
    public int count = 0;
    String groupId = "";
    public boolean mIsLoading = false;
    public int totalPages = 1;
    public int currentPage = 1;
    String name = "";
    String searchString = "";
    LeadItem item = new LeadItem();
    String selectID = "0";

    public TreeListFragment() {

    }

    public static TreeListFragment newInstance(Bundle b) {
        TreeListFragment fragment = new TreeListFragment();
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_list_without_refresh, container, false);
        mBinding.setSize(1);
        mBinding.setMessage(R.string.msg_no_leads);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        int count = databaseHandler.getCount();
        mAdapter = new LeadAdapter(new ArrayList<LeadItem>(), this, 3, databaseHandler, count,false,false);
        groupId = getArguments().getString("id", "");
        item = getArguments().getParcelable("extra_lead_item");
        name = item.getName();
        selectID = item.getId();
        getData(item.getId());
        mBinding.txtLevel.setText("Level : " + count);
        getActivity().setTitle(name);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(manager);
        mBinding.txtLevel.setVisibility(View.GONE);
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = manager.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();

                if (!mIsLoading && totalPages > currentPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            ) {
                        if (searchString.equals("")) {
                            currentPage = currentPage + 1;
                            getData(selectID);
                        }
                    }
                }
            }
        });

        return mBinding.getRoot();
    }

    public void getData(String id) {
        showLoadingBar(mBinding.progressBar);
        mIsLoading = true;
        LeafManager manager = new LeafManager();
        manager.getNestedFriends(this, groupId+"", id, currentPage);

    }
    public void getServerFilter(String searchStr,int count)
    {
        if(count==0)
        {
            mAdapter.clear();
            currentPage=1;
            showLoadingBar(mBinding.progressBar);
            mIsLoading = true;
            LeafManager manager = new LeafManager();
            manager.getNestedFriends(this, groupId+"", selectID, currentPage);
        }
        else {
            mAdapter.clear();
            currentPage=1;
            showLoadingBar(mBinding.progressBar);
            mIsLoading = true;
            LeafManager manager = new LeafManager();
            manager.getRefferalFilter(this, groupId+"", selectID, searchStr);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
       AppLog.e("sdfas", "onRequestPermissionsResult " + requestCode);
        switch (requestCode) {
            case REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   AppLog.e("sdfas", "gra " + requestCode);
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                   AppLog.e("sdfas", "not gr " + requestCode);
                }
                return;

            case REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (intent != null) {
                        startActivity(intent);
                    } else {
                       AppLog.e("sdfas", "not gr " + requestCode);
                    }
                    return;
                }
        }
    }

    @Override
    public void onCallClick(LeadItem item) {
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + item.getPhone()));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
           AppLog.e("sdfas", "per");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
           AppLog.e("sdfas", "acti");
            startActivity(intent);
        }
    }

    @Override
    public void onSMSClick(LeadItem item) {
        intent = new Intent(getActivity(), AddPostActivity.class);
        AppLog.e(TAG, "onSMSClick group_id " + groupId);
        intent.putExtra("id", groupId);
        intent.putExtra("friend_id", item.getId());
        intent.putExtra("type", "personal");
        intent.putExtra("team_id", selectID);
        startActivity(intent);
        LeafPreference.getInstance(getActivity()).setBoolean(LeafPreference.PERSONAL_POST_ADDED_1,true);
        getActivity().finish();
    }

    @Override
    public void onMailClick(LeadItem item) {
        mAdapter.clear();
        name = item.getName();
        selectID = item.getId();
        currentPage = 1;
        getData(item.getId());
    }

    @Override
    public void onNameClick(LeadItem item) {
        Intent intent = new Intent(getActivity(), LeadDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(LeadDetailActivity.EXTRA_LEAD_ITEM, item);
        intent.putExtras(b);
        intent.putExtra("type", "nestedfriend");
        intent.putExtra("team_id", selectID);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null) return;
        hideLoadingBar();
        count++;
        mBinding.txtLevel.setText("Level : " + count);
        getActivity().setTitle(name);
        LeadResponse res = (LeadResponse) response;
        mAdapter.addItems(res.getResults());
        mAdapter.notifyDataSetChanged();
        if (currentPage == 1)
            mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.setSize(mAdapter.getItemCount());

        totalPages = res.totalNumberOfPages;
        mIsLoading = false;
        mBinding.txtLevel.setVisibility(View.GONE);
        hide_keyboard();

    }

    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        try {
            if (msg.contains("401:Unauthorized")) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
                logout();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
            hide_keyboard();
        }catch (Exception e){}

    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        try {
            Toast.makeText(getActivity(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
           AppLog.e("TreeListFrag", "onException : " + msg);
        }
        hide_keyboard();
    }

    public void getFilteredList(String str) {
        searchString = str;
        mAdapter.getFilter().filter(str);
    }

}*/
