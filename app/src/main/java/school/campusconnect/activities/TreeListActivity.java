package school.campusconnect.activities;


public class TreeListActivity extends BaseActivity {
/*
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.edtSearch)
    EditText edtSearch;
    Intent intent;
    TreeListFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_my_leads);
        searchListener();
        fragment = TreeListFragment.newInstance(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void searchListener() {
        edtSearch.setCursorVisible(false);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("GroupDashboard", "onClick ");
                edtSearch.setCursorVisible(true);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    edtSearch.setCursorVisible(false);
                    if (edtSearch.getText().toString().isEmpty()) {
                        Toast.makeText(TreeListActivity.this, "Input some query first", Toast.LENGTH_LONG).show();
                    } else {
                        fragment.getFilteredList(edtSearch.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().length() > 0) {
                    fragment.getServerFilter(edtSearch.getText().toString(),count);
                } else if (edtSearch.getText().toString().length() == 0) {
                    fragment.getServerFilter(edtSearch.getText().toString(),count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }*/

}
