package fairy.easy.httpcanary.preview;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fairy.easy.httpcanary.HttpCanary;
import fairy.easy.httpcanary.R;

public class PreviewAdapter extends BaseAdapter implements Filterable {

    private final Context mContext;
    private List<HarEntry> harEntryList = new ArrayList<>();
    private HarLog harLog;

    public PreviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void notifyHarChange() {
        harEntryList.clear();
        notifyDataSetChanged();
    }

    public void setList() {
        if (HttpCanary.getHttpCanaryFactory().getInitProxy()) {
            harLog = HttpCanary.getHttpCanaryFactory().getProxy().getHar().getLog();
            harEntryList.clear();
            harEntryList.addAll(harLog.getEntries());
        }
//        notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HarEntry harEntry = harEntryList.get(position);
        if (harLog.getEntries().indexOf(harEntry) >= 0) {
            Intent intent = new Intent(mContext, HarDetailActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("pos", HttpCanary.getHttpCanaryFactory().getProxy().
                    getHar().getLog().getEntries().indexOf(harEntry));
            mContext.startActivity(intent);
        }
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
            holder.appIcon = convertView.findViewById(R.id.http_canary_iv_app);
            holder.name = convertView.findViewById(R.id.http_canary_tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HarEntry harEntry = harEntryList.get(position);
        holder.tv.setText(harEntry.getRequest().getUrl());
        holder.name.setText(TextUtils.isEmpty(harEntry.getPort2PackageName().getAppName()) ? "未知" : harEntry.getPort2PackageName().getAppName());
        if (harEntry.getResponse().getStatus() > 400) {
            holder.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_error_black_24dp));
        } else if (harEntry.getResponse().getStatus() > 300) {
            holder.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_directions_black_24dp));
        } else if (harEntry.getResponse().getContent().getMimeType().contains("image")) {
            holder.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_photo_black_24dp));
        } else {
            holder.iconView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_description_black_24dp));
        }
        try {
            if(harEntry.getPort2PackageName().getIcon()!=null){
                holder.appIcon.setImageDrawable(harEntry.getPort2PackageName().getIcon());
            }else {
                holder.appIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_error_black_24dp));
            }
        }catch (Exception e){
            holder.appIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.http_canary_ic_error_black_24dp));
        }
        holder.detailTextView.setText(String.format("Status:%d Size:%dBytes Time:%dms\n%s", harEntry.getResponse().getStatus(), harEntry.getResponse().getBodySize(), harEntry.getTime(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
                .format(harEntry.getStartedDateTime().getTime())));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //初始化过滤结果对象
                FilterResults results = new FilterResults();
                //假如搜索为空的时候，将复制的数据添加到原始数据，用于继续过滤操作
                if (results.values == null) {
                    harEntryList.clear();
                    harEntryList.addAll(harLog.getEntries());
                }
                //关键字为空的时候，搜索结果为复制的结果
                if (constraint == null || constraint.length() == 0) {
                    results.values = harLog.getEntries();
                    results.count = harLog.getEntries().size();
                } else {
                    String prefixString = constraint.toString();
                    final int count = harEntryList.size();
                    //用于存放暂时的过滤结果
                    final ArrayList<HarEntry> newValues = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        final HarEntry value = harEntryList.get(i);
                        String url = value.getRequest().getUrl();
                        // 假如含有关键字的时候，添加
                        if (url.contains(prefixString)) {
                            newValues.add(value);
                        } else {
                            //过来空字符开头
                            String[] words = prefixString.split(" ");

                            for (String word : words) {
                                if (url.contains(word)) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //清除原始数据
                harEntryList.clear();
                if (results.values instanceof List) {
                    //将过滤结果添加到这个对象
                    harEntryList.addAll((List<HarEntry>) results.values);
                }
                if (results.count > 0) {
                    //有关键字的时候刷新数据
                    notifyDataSetChanged();
                } else {
                    //关键字不为零但是过滤结果为空刷新数据
                    if (constraint.length() != 0) {
                        notifyDataSetChanged();
                        return;
                    }
                    //加载复制的数据，即为最初的数据
                    harEntryList.addAll(harLog.getEntries());
                    notifyDataSetChanged();
                }
            }
        };
    }

    class Holder {
        private TextView tv;
        private TextView detailTextView;
        private ImageView iconView;
        private ImageView appIcon;
        private TextView name;

    }
}
