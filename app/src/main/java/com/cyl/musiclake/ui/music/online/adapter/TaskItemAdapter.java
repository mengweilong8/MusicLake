package com.cyl.musiclake.ui.music.online.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.RxBus;
import com.cyl.musiclake.common.NavigateUtil;
import com.cyl.musiclake.data.source.download.TasksManager;
import com.cyl.musiclake.data.source.download.TasksManagerModel;
import com.cyl.musiclake.utils.FileUtils;
import com.cyl.musiclake.utils.LogUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonglong on 2018/1/23.
 */

public class TaskItemAdapter extends BaseQuickAdapter<TasksManagerModel, TaskItemAdapter.TaskItemViewHolder> {
    private static final String TAG = "TaskItemAdapter";
    private List<TasksManagerModel> models = new ArrayList<>();

    public TaskItemAdapter(List<TasksManagerModel> models) {
        super(R.layout.item_download_music, models);
        this.models = models;
    }

    private FileDownloadListener taskDownloadListener = new FileDownloadListener();

    private View.OnClickListener taskActionOnClickListener = v -> {
        if (v.getTag() == null) {
            return;
        }

        TaskItemViewHolder holder = (TaskItemViewHolder) v.getTag();

        CharSequence action = ((TextView) v).getText();
        if (action.equals(v.getResources().getString(R.string.pause))) {
            // to pause
            FileDownloader.getImpl().pause(holder.id);
        } else if (action.equals(v.getResources().getString(R.string.start))) {
            // to start
            // to start
            final TasksManagerModel model = TasksManager.getImpl().get(holder.position);
            final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                    .setPath(model.getPath())
                    .setCallbackProgressTimes(100)
                    .setListener(taskDownloadListener);

            TasksManager.getImpl()
                    .addTaskForViewHolder(task);

            TasksManager.getImpl()
                    .updateViewHolder(holder.id, holder);

            task.start();
        } else if (action.equals(v.getResources().getString(R.string.delete))) {
            // to delete
            new File(TasksManager.getImpl().get(holder.position).getPath()).delete();
            holder.taskActionBtn.setEnabled(true);
            holder.updateNotDownloaded(FileDownloadStatus.INVALID_STATUS, 0, 0);
        }
    };

//    @Override
//    protected TaskItemViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
//        TaskItemViewHolder holder = new TaskItemViewHolder(
//                LayoutInflater.from(
//                        parent.getContext())
//                        .inflate(R.layout.item_download_music, parent, false));
//        return holder;
//    }

    @Override
    protected void convert(TaskItemViewHolder holder, TasksManagerModel model) {
        holder.taskActionBtn.setOnClickListener(taskActionOnClickListener);
        holder.update(model.getId(), holder.getAdapterPosition());
        holder.taskActionBtn.setTag(holder);
        holder.taskNameTv.setText(model.getName());

        TasksManager.getImpl()
                .updateViewHolder(holder.id, holder);

        holder.taskActionBtn.setEnabled(true);

        if (TasksManager.getImpl().isReady()) {
            final int status = TasksManager.getImpl().getStatus(model.getId(), model.getPath());
            if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
                    status == FileDownloadStatus.connected) {
                // start task, but file not created yet
                holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));
            } else if (!new File(model.getPath()).exists() &&
                    !new File(FileDownloadUtils.getTempPath(model.getPath())).exists()) {
                // not exist file
                holder.updateNotDownloaded(status, 0, 0);
            } else if (TasksManager.getImpl().isDownloaded(status)) {
                // already downloaded and exist
                LogUtil.e(TAG, "already downloaded and exist");
                holder.updateDownloaded();
                TasksManager.getImpl().finishTask(model.getId());
            } else if (status == FileDownloadStatus.progress) {
                // downloading
                holder.updateDownloading(status, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));
            } else {
                // not start
                holder.updateNotDownloaded(status, TasksManager.getImpl().getSoFar(model.getId())
                        , TasksManager.getImpl().getTotal(model.getId()));
            }
        } else {
            holder.taskStatusTv.setText(R.string.tasks_manager_demo_status_loading);
            holder.taskActionBtn.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class TaskItemViewHolder extends BaseViewHolder {
        public TaskItemViewHolder(View itemView) {
            super(itemView);
            assignViews();
        }

        private View findViewById(final int id) {
            return itemView.findViewById(id);
        }

        /**
         * viewHolder position
         */
        private int position;
        /**
         * download id
         */
        public int id;

        public void update(final int id, final int position) {
            this.id = id;
            this.position = position;
        }

        public void updateDownloaded() {
            taskPb.setMax(1);
            taskPb.setProgress(1);
            taskStatusTv.setText(R.string.tasks_manager_demo_status_completed);
            taskActionBtn.setText(R.string.delete);
            taskActionBtn.setVisibility(View.GONE);
            taskPb.setVisibility(View.GONE);
            NavigateUtil.scanFileAsync(MusicApp.mContext, FileUtils.getMusicDir());
            RxBus.getInstance().post(new TasksManagerModel());
        }

        public void updateNotDownloaded(final int status, final long sofar, final long total) {
            if (sofar > 0 && total > 0) {
                final float percent = sofar
                        / (float) total;
                taskPb.setMax(100);
                taskPb.setProgress((int) (percent * 100));
            } else {
                taskPb.setMax(1);
                taskPb.setProgress(0);
            }

            switch (status) {
                case FileDownloadStatus.error:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_error);
                    break;
                case FileDownloadStatus.paused:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_paused);
                    break;
                default:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_not_downloaded);
                    break;
            }
            taskActionBtn.setText(R.string.start);
        }

        public void updateDownloading(final int status, final long sofar, final long total) {
            final float percent = sofar
                    / (float) total;
            taskPb.setMax(100);
            taskPb.setProgress((int) (percent * 100));

            switch (status) {
                case FileDownloadStatus.pending:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_pending);
                    break;
                case FileDownloadStatus.started:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_started);
                    break;
                case FileDownloadStatus.connected:
                    taskStatusTv.setText(R.string.tasks_manager_demo_status_connected);
                    break;
//                case FileDownloadStatus.progress:
//                    taskStatusTv.setText(R.string.tasks_manager_demo_status_progress);
//                    break;
                default:
                    taskStatusTv.setText(MusicApp.mContext.getString(
                            R.string.tasks_manager_demo_status_downloading, (int) (percent * 100)));
                    break;
            }

            taskActionBtn.setText(R.string.pause);
        }

        public TextView taskNameTv;
        public TextView taskStatusTv;
        public ProgressBar taskPb;
        public Button taskActionBtn;

        private void assignViews() {
            taskNameTv = (TextView) findViewById(R.id.task_name_tv);
            taskStatusTv = (TextView) findViewById(R.id.task_status_tv);
            taskPb = (ProgressBar) findViewById(R.id.task_pb);
            taskActionBtn = (Button) findViewById(R.id.task_action_btn);
        }
    }
}

