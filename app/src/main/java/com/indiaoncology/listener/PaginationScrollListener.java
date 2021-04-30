package com.indiaoncology.listener;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private GridLayoutManager gridlayoutManager;
    private LinearLayoutManager layoutManager;
    private String type;

    public PaginationScrollListener(GridLayoutManager layoutManager, String type) {
        this.gridlayoutManager = layoutManager;
        this.type = type;
    }

    public PaginationScrollListener(LinearLayoutManager layoutManager, String type) {
        this.layoutManager = layoutManager;
        this.type = type;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount, totalItemCount, firstVisibleItemPosition;
        if (type.equals("GRID")) {
            visibleItemCount = gridlayoutManager.getChildCount();
            totalItemCount = gridlayoutManager.getItemCount();
            firstVisibleItemPosition = gridlayoutManager.findFirstVisibleItemPosition();

        } else {
            visibleItemCount = layoutManager.getChildCount();
            totalItemCount = layoutManager.getItemCount();
            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        }

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
