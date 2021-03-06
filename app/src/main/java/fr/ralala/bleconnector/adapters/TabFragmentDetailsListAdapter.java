package fr.ralala.bleconnector.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import fr.ralala.bleconnector.BleConnectorApplication;
import fr.ralala.bleconnector.R;
import fr.ralala.bleconnector.utils.gatt.GattHelper;

/********************************************************************************
 * <p><b>Project BleConnector</b><br/>
 * Adapter used with the services list view.
 * </p>
 *
 * @author Keidan
 * <p>
 *******************************************************************************/
public class TabFragmentDetailsListAdapter extends BaseExpandableListAdapter {
  private final Context mContext;
  private List<BluetoothGattService> mListDataHeader; // header titles
  // child data in format of header title, child title
  private HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> mListDataChild;
  private LayoutInflater mInflater;
  private final ViewGroup mNullParent = null;
  private OnImageClick mOnImageClick;
  private ExpandableListView mExpandableListView;

  private class ViewHolderHeader {
    TextView tvName;
    TextView tvUUID;
  }

  private class ViewHolderChild {
    TextView tvName;
    TextView tvUUID;
    TextView tvProperties;
    TextView tvData;
    TableRow trData;
    ImageView ivDownload;
    ImageView ivUpload;
  }


  public interface OnImageClick {
    /**
     * Called when the user clicks on an image.
     *
     * @param download      True if the download image is clicked, false for the upload image.
     * @param groupPosition The position of the group that the child resides in.
     * @param childPosition The position of the child with respect to other children in the group.
     */
    void onImageClick(boolean download, int groupPosition, int childPosition);
  }

  /**
   * Creates the array adapter.
   *
   * @param context The Android context.
   */
  public TabFragmentDetailsListAdapter(final Context context, ExpandableListView ExpandableListView, List<BluetoothGattService> listDataHeader,
                                       HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> listDataChild, OnImageClick imageClick) {
    mContext = context;
    mExpandableListView = ExpandableListView;
    mListDataChild = listDataChild;
    mListDataHeader = listDataHeader;
    mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    mOnImageClick = imageClick;
  }

  /**
   * Clears the lists.
   */
  public void clear() {
    mListDataHeader.clear();
    mListDataChild.clear();
  }

  /**
   * Adds a new service.
   *
   * @param service The service to add.
   */
  public void add(BluetoothGattService service) {
    mListDataHeader.add(service);
    mListDataChild.put(service, service.getCharacteristics());
  }

  public void expandAll() {
    for (int i = getGroupCount() - 1; i >= 0; i--)
      mExpandableListView.expandGroup(i);
  }

  public void collapseAll() {
    for (int i = getGroupCount() - 1; i >= 0; i--)
      mExpandableListView.collapseGroup(i);
  }

  /**
   * Gets the data associated with the given child within the given group.
   *
   * @param groupPosition The position of the group that the child resides in.
   * @param childPosition The position of the child with respect to other children in the group.
   * @return The data of the child.
   */
  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return mListDataChild.get(mListDataHeader.get(groupPosition))
        .get(childPosition);
  }

  /**
   * Gets the ID for the given child within the given group.
   *
   * @param groupPosition The position of the group that contains the child.
   * @param childPosition The position of the child within the group for which the ID is wanted.
   * @return The ID associated with the child.
   */
  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  /**
   * Gets a View that displays the data for the given child within the given group.
   *
   * @param groupPosition The position of the group that contains the child
   * @param childPosition The position of the child (for which the View is returned) within the group.
   * @param isLastChild   Whether the child is the last child within the group.
   * @param convertView   The old view to reuse, if possible.
   * @param parent        The parent that this view will eventually be attached to.
   * @return The View corresponding to the child at the specified position.
   */
  @Override
  public View getChildView(int groupPosition, final int childPosition,
                           boolean isLastChild, View convertView, ViewGroup parent) {

    final BluetoothGattCharacteristic child = (BluetoothGattCharacteristic) getChild(groupPosition, childPosition);
    ViewHolderChild holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.expandable_list_child_details, mNullParent);
      holder = new ViewHolderChild();
      holder.tvName = convertView.findViewById(R.id.tvName);
      holder.tvUUID = convertView.findViewById(R.id.tvUUID);
      holder.tvProperties = convertView.findViewById(R.id.tvProperties);
      holder.tvData = convertView.findViewById(R.id.tvData);
      holder.trData = convertView.findViewById(R.id.trData);
      holder.ivDownload = convertView.findViewById(R.id.ivDownload);
      holder.ivUpload = convertView.findViewById(R.id.ivUpload);
      holder.ivDownload.setOnClickListener((view) -> mOnImageClick.onImageClick(true, groupPosition, childPosition));
      holder.ivUpload.setOnClickListener((view) -> mOnImageClick.onImageClick(false, groupPosition, childPosition));
      convertView.setTag(holder);
    } else {
      holder = (ViewHolderChild) convertView.getTag();
    }
    String uuid = child.getUuid().toString();

    holder.tvName.setText(BleConnectorApplication.getInstance().getGattHelper().lookup(uuid, mContext.getString(R.string.unknown_characteristic), false));
    holder.tvUUID.setText(GattHelper.fixUUID(uuid));
    String properties;
    int prop = child.getProperties();
    if (child.getProperties() != 0) {
      properties = "";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0)
        properties += mContext.getString(R.string.property_broadcast) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) != 0)
        properties += mContext.getString(R.string.property_read) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)
        properties += mContext.getString(R.string.property_write_no_response) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0)
        properties += mContext.getString(R.string.property_write) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
        properties += mContext.getString(R.string.property_notify) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0)
        properties += mContext.getString(R.string.property_indicate) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0)
        properties += mContext.getString(R.string.property_signed_write) + ",";
      if ((prop & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0)
        properties += mContext.getString(R.string.property_extended_props) + ",";
      if (properties.endsWith(","))
        properties = properties.substring(0, properties.length() - 1);
    } else
      properties = mContext.getString(R.string.unknown);
    holder.tvProperties.setText(properties);
    int props = child.getProperties();
    if ((props & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0 &&
        (props & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
      holder.trData.setVisibility(View.GONE);
    } else {
      holder.trData.setVisibility(View.VISIBLE);
      byte[] bytes = child.getValue();
      holder.tvData.setText(bytes == null ? "" : BleConnectorApplication.getInstance().getGattHelper().convert(uuid, bytes));
    }
    if ((props & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 ||
        (props & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0 ||
        (props & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) != 0)
      holder.ivUpload.setVisibility(View.VISIBLE);
    else
      holder.ivUpload.setVisibility(View.INVISIBLE);

    if ((props & BluetoothGattCharacteristic.PROPERTY_READ) != 0)
      holder.ivDownload.setVisibility(View.VISIBLE);
    else
      holder.ivDownload.setVisibility(View.INVISIBLE);
    return convertView;
  }

  /**
   * Gets the number of children in a specified group.
   *
   * @param groupPosition The position of the group for which the children count should be returned.
   * @return The children count in the specified group
   */
  @Override
  public int getChildrenCount(int groupPosition) {
    return mListDataChild.get(mListDataHeader.get(groupPosition))
        .size();
  }

  /**
   * Gets the data associated with the given group.
   *
   * @param groupPosition The position of the group.
   * @return The data child for the specified group.
   */
  @Override
  public Object getGroup(int groupPosition) {
    return mListDataHeader.get(groupPosition);
  }

  /**
   * Gets the number of groups.
   *
   * @return The number of groups.
   */
  @Override
  public int getGroupCount() {
    return mListDataHeader.size();
  }

  /**
   * Gets the ID for the group at the given position.
   *
   * @param groupPosition The position of the group for which the ID is wanted.
   * @return The ID associated with the group.
   */
  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  /**
   * Gets a View that displays the given group.
   *
   * @param groupPosition The position of the group for which the View is returned.
   * @param isExpanded    Whether the group is expanded or collapsed.
   * @param convertView   The old view to reuse, if possible.
   * @param parent        The parent that this view will eventually be attached to.
   * @return The View corresponding to the group at the specified position.
   */
  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    BluetoothGattService header = (BluetoothGattService) getGroup(groupPosition);
    ViewHolderHeader holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.expandable_list_header_details, mNullParent);
      holder = new ViewHolderHeader();
      holder.tvName = convertView.findViewById(R.id.tvName);
      holder.tvUUID = convertView.findViewById(R.id.tvUUID);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolderHeader) convertView.getTag();
    }

    String uuid = header.getUuid().toString();
    holder.tvName.setText(BleConnectorApplication.getInstance().getGattHelper().lookup(uuid, mContext.getString(R.string.unknown_service), true));
    holder.tvUUID.setText(GattHelper.fixUUID(uuid));
    return convertView;
  }

  /**
   * Indicates whether the child and group IDs are stable across changes to the underlying data.
   *
   * @return Whether or not the same ID always refers to the same object.
   */
  @Override
  public boolean hasStableIds() {
    return false;
  }

  /**
   * Whether the child at the specified position is selectable.
   *
   * @param groupPosition The position of the group that contains the child.
   * @param childPosition The position of the child within the group.
   * @return Whether the child is selectable.
   */
  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
}

