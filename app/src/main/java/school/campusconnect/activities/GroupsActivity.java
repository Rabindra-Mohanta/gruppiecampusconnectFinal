package school.campusconnect.activities;

public class GroupsActivity extends BaseActivity {
/*
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.rvGrpList)
    public RecyclerView rvGrpList;

    ContactListItem contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_groups);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        contact = getIntent().getParcelableExtra("gruppie_contact");

        setTitle(contact.getName());

        ((TextView) findViewById(R.id.txt_name)).setText(contact.getName());
        ((TextView) findViewById(R.id.txt_phone)).setText(contact.getPhone());


        ImageView imgLead = (ImageView) findViewById(R.id.img_lead_default);
        // txtOtherLeads.setText(TextUtils.join("\n", mLeadItem.getOtherLeads()));
        if (contact.getImage() != null && !contact.getImage().isEmpty()) {
            Picasso.with(this).load(contact.getImage()).into(((ImageView) findViewById(R.id.img_lead)));
        }
        else
        {
            imgLead.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(ImageUtil.getTextLetter(contact.getName()), ImageUtil.getRandomColor(1));
            imgLead.setImageDrawable(drawable);
        }

        rvGrpList.setNestedScrollingEnabled(false);
        rvGrpList.setFocusable(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvGrpList.setLayoutManager(mLayoutManager);
        GroupListAdapter retailerAdapter = new GroupListAdapter(this, getIntent().getExtras().getStringArray("groups"));
        rvGrpList.setAdapter(retailerAdapter);

    }*/
}
