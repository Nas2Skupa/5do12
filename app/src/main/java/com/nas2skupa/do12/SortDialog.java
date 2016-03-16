package com.nas2skupa.do12;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

/**
 * Created by ranko on 15/03/16.
 */
public class SortDialog extends AlertDialog {
    private final View akcijeChbox;
    private ProviderAdapter.SortType sortType = ProviderAdapter.SortType.NONE;
    private boolean filterDiscounts = false;

    public ProviderAdapter.SortType getSortType() {
        return sortType;
    }

    public boolean shouldFilterDiscounts() {
        return filterDiscounts;
    }

    public void showDiscountsFilter(boolean show) {
        akcijeChbox.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected SortDialog(final Context context, final ProviderAdapter adapter) {
        super(context);

        setTitle("Poredaj pružatelje usluga");

        final View view = getLayoutInflater().inflate(R.layout.list_settings, null);
        akcijeChbox = view.findViewById(R.id.akcijeChbox);
        setView(view);

        setButton(BUTTON_POSITIVE, "Primjeni", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                CheckBox actions = (CheckBox) ((AlertDialog) dialog).findViewById(R.id.akcijeChbox);
                filterDiscounts = actions.isChecked();
                adapter.filterDiscounts(filterDiscounts);

                RadioGroup radioGroup = (RadioGroup) ((AlertDialog) dialog).findViewById(R.id.sortGrp);
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.nazivRbtn:
                        sortType = ProviderAdapter.SortType.NAME;
                        break;
                    case R.id.najnovijeRbtn:
                        sortType = ProviderAdapter.SortType.LATEST;
                        break;
                    case R.id.ocjeneRbtn:
                        sortType = ProviderAdapter.SortType.RATING;
                        break;
                    case R.id.udaljenostRbtn:
                        sortType = ProviderAdapter.SortType.DISTANCE;
                        break;
                }
                adapter.sortBy(sortType);
                adapter.notifyDataSetChanged();
            }
        });
        setButton(BUTTON_NEGATIVE, getContext().getString(R.string.close), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}
