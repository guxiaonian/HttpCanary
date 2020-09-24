package fairy.easy.httpcanary.preview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.lightbody.bmp.core.har.HarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fairy.easy.httpcanary.R;

public class PreviewAdapter extends BaseAdapter {

    private final Context mContext;
    private List<HarEntry> harEntryList = new ArrayList<>();

    public PreviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void notifyHarChange() {
        harEntryList.clear();
        notifyDataSetChanged();
    }

    public void addList(HarEntry harEntry) {
        harEntryList.add(harEntry);
    }

    public static HarEntry harEntry;

    public static HarEntry getHarEntry() {
        return harEntry;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        harEntry = harEntryList.get(position);
        Intent intent = new Intent(mContext, HarDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int getCount() {
        return harEntryList.size();
    }

    @Override
    public Object getItem(int position) {
        return harEntryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.http_canary_item_preview, null);
            holder = new Holder();
            holder.tv = convertView.findViewById(R.id.http_canary_tv_url);
            holder.detailTextView = convertView.findViewById(R.id.http_canary_tv_detail);
            holder.iconView = convertView.findViewById(R.id.http_canary_iv_icon);
            holder.name = convertView.findViewById(R.id.http_canary_tv_name);
            holder.imageViewTitle = convertView.findViewById(R.id.http_canary_iv_title);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HarEntry harEntry = harEntryList.get(position);
        holder.tv.setText(harEntry.getRequest().getUrl());
        if (harEntry.getPort2PackageName() != null && harEntry.getPort2PackageName().getIcon() != null && !TextUtils.isEmpty(harEntry.getPort2PackageName().getAppName())) {
            holder.name.setText(harEntry.getPort2PackageName().getAppName());
            holder.iconView.setImageDrawable(harEntry.getPort2PackageName().getIcon());
        } else {
            holder.name.setText("Unknown");
            holder.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_error_black_24dp));
        }
        if (harEntry.getResponse().getContent().getMimeType().contains("image")) {
            holder.imageViewTitle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_photo_black_24dp));
        } else {
            holder.imageViewTitle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_description_black_24dp));
        }
        holder.detailTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(harEntry.getStartedDateTime().getTime()));
        return convertView;
    }

    class Holder {
        private TextView tv;
        private TextView detailTextView;
        private ImageView iconView;
        private TextView name;
        private ImageView imageViewTitle;

    }
}
