package com.media.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import com.media.R;
import com.media.config.PictureConfig;
import com.media.config.PictureMimeType;
import com.media.entity.LocalMedia;
import com.media.entity.LocalMediaFolder;
import com.media.tools.SdkVersionUtils;
import com.media.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author：luck
 * @data：2016/12/31 19:12
 * @describe: Local media database query class
 */
public final class LocalMediaLoader {
    private boolean isGif=true;
    private boolean isWebp=true;
    private boolean isBmp=true;
    private long filterFileSize=100;
    private long filterMaxFileSize=100000000;
    private long filterMinFileSize=0;
    private int videoMinSecond=0;
    private int videoMaxSecond=200000000;
    private int chooseMode=PictureMimeType.ofAll();
    private static final String TAG = LocalMediaLoader.class.getSimpleName();
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF_UNKNOWN = "!='image/*'";
    private static final String NOT_GIF = " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN + ")";

    private final Context mContext;
    private final boolean isAndroidQ;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;


    /**
     * Media file database field
     */
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED};

    /**
     * Video or Audio mode conditions
     *
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForVideoOrAudioMediaCondition(String sizeCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + sizeCondition;
    }

    /**
     * Query conditions in all modes
     *
     * @param timeCondition
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String timeCondition, String sizeCondition, String queryMimeCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=?").append(queryMimeCondition).append(" OR ")
                .append(MediaStore.Files.FileColumns.MEDIA_TYPE).append("=? AND ").append(timeCondition).append(") AND ").append(sizeCondition).toString();
        LogUtils.INSTANCE.e(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * Query conditions in image modes
     *
     * @param sizeCondition
     * @param queryMimeCondition
     * @return
     */
    private static String getSelectionArgsForImageMediaCondition(String sizeCondition, String queryMimeCondition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + sizeCondition;
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private static String[] getSelectionArgsForAllMediaType() {
        return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
    }

    /**
     * Gets a file of the specified type
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }


    public LocalMediaLoader(Context context) {
        this.mContext = context.getApplicationContext();
        this.isAndroidQ = SdkVersionUtils.checkedAndroid_Q();
    }

    /**
     * Query the local gallery data
     *
     * @return
     */
    public ArrayList<LocalMediaFolder> loadAllMedia() {
        Cursor data = mContext.getContentResolver().query(QUERY_URI, PROJECTION, getSelection(), getSelectionArgs(), ORDER_BY);
        try {
            if (data != null) {
                ArrayList<LocalMediaFolder> imageFolders = new ArrayList<>();
                LocalMediaFolder allImageFolder = new LocalMediaFolder();
                ArrayList<LocalMedia> latelyImages = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    int idColumn = data.getColumnIndexOrThrow(PROJECTION[0]);
                    int dataColumn = data.getColumnIndexOrThrow(PROJECTION[1]);
                    int mimeTypeColumn = data.getColumnIndexOrThrow(PROJECTION[2]);
                    int widthColumn = data.getColumnIndexOrThrow(PROJECTION[3]);
                    int heightColumn = data.getColumnIndexOrThrow(PROJECTION[4]);
                    int durationColumn = data.getColumnIndexOrThrow(PROJECTION[5]);
                    int sizeColumn = data.getColumnIndexOrThrow(PROJECTION[6]);
                    int folderNameColumn = data.getColumnIndexOrThrow(PROJECTION[7]);
                    int fileNameColumn = data.getColumnIndexOrThrow(PROJECTION[8]);
                    int bucketIdColumn = data.getColumnIndexOrThrow(PROJECTION[9]);
                    int dateAddedColumn = data.getColumnIndexOrThrow(PROJECTION[10]);

                    data.moveToFirst();
                    do {
                        long id = data.getLong(idColumn);
                        String mimeType = data.getString(mimeTypeColumn);
                        mimeType = TextUtils.isEmpty(mimeType) ? PictureMimeType.ofJPEG() : mimeType;
                        String absolutePath = data.getString(dataColumn);
                        String url = isAndroidQ ? PictureMimeType.getRealPathUri(id,mimeType) : absolutePath;
                        // Here, it is solved that some models obtain mimeType and return the format of image / *,
                        // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                        if (mimeType.endsWith("image/*")) {
                            if (PictureMimeType.isContent(url)) {
                                mimeType = PictureMimeType.getImageMimeType(absolutePath);
                            } else {
                                mimeType = PictureMimeType.getImageMimeType(url);
                            }
                            if (!isGif) {
                                boolean isGif = PictureMimeType.isGif(mimeType);
                                if (isGif) {
                                    continue;
                                }
                            }
                        }
                        if (!isWebp) {
                            if (mimeType.startsWith(PictureMimeType.ofWEBP())) {
                                continue;
                            }
                        }
                        if (!isBmp) {
                            if (mimeType.startsWith(PictureMimeType.ofBMP())) {
                                continue;
                            }
                        }

                        int width = data.getInt(widthColumn);
                        int height = data.getInt(heightColumn);
                        long duration = data.getLong(durationColumn);
                        long size = data.getLong(sizeColumn);
                        String folderName = data.getString(folderNameColumn);
                        String fileName = data.getString(fileNameColumn);
                        long bucketId = data.getLong(bucketIdColumn);
                        if (filterFileSize > 0) {
                            if (size > filterFileSize * FILE_SIZE_UNIT) {
                                continue;
                            }
                        }

                        if (PictureMimeType.isHasVideo(mimeType)) {
                            if (videoMinSecond > 0 && duration < videoMinSecond) {
                                // If you set the minimum number of seconds of video to display
                                continue;
                            }
                            if (videoMaxSecond > 0 && duration > videoMaxSecond) {
                                // If you set the maximum number of seconds of video to display
                                continue;
                            }
                            if (duration == 0) {
                                //If the length is 0, the corrupted video is processed and filtered out
                                continue;
                            }
                            if (size <= 0) {
                                // The video size is 0 to filter out
                                continue;
                            }
                        }
                        LocalMedia image = LocalMedia.parseLocalMedia(id, url, absolutePath, fileName, folderName, duration, chooseMode, mimeType, width, height, size, bucketId, data.getLong(dateAddedColumn));
                        LocalMediaFolder folder = getImageFolder(url,mimeType, folderName,bucketId,imageFolders);
                        folder.setBucketId(image.getBucketId());
                        List<LocalMedia> images = folder.getData();
                        images.add(image);
                        folder.setImageNum(folder.getImageNum() + 1);
                        folder.setBucketId(image.getBucketId());
                        latelyImages.add(image);
                        int imageNum = allImageFolder.getImageNum();
                        allImageFolder.setImageNum(imageNum + 1);

                    } while (data.moveToNext());

                    if (latelyImages.size() > 0) {
                        sortFolder(imageFolders);
                        imageFolders.add(0, allImageFolder);
                        allImageFolder.setFirstImagePath
                                (latelyImages.get(0).getPath());
                        allImageFolder.setFirstMimeType(latelyImages.get(0).getMimeType());
                        String title = chooseMode == PictureMimeType.ofAudio() ?
                                mContext.getString(R.string.picture_all_audio)
                                : mContext.getString(R.string.picture_camera_roll);
                        allImageFolder.setName(title);
                        allImageFolder.setBucketId(-1);
                        allImageFolder.setOfAllType(chooseMode);
                        allImageFolder.setCameraFolder(true);
                        allImageFolder.setData(latelyImages);
                    }
                }
                return imageFolders;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "loadAllMedia Data Error: " + e.getMessage());
            return null;
        } finally {
            if (data != null && !data.isClosed()) {
                data.close();
            }
        }
        return null;
    }

    private String getSelection() {
        String durationCondition = getDurationCondition();
        String fileSizeCondition = getFileSizeCondition();
        String queryMimeCondition = getQueryMimeCondition();
        switch (chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get all, not including audio
                return getSelectionArgsForAllMediaCondition(durationCondition, fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_IMAGE:
                // Gets the image
                return getSelectionArgsForImageMediaCondition(fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_VIDEO:
                // Access to video
                return getSelectionArgsForVideoOrAudioMediaCondition(fileSizeCondition, queryMimeCondition);
            case PictureConfig.TYPE_AUDIO:
                // Access to the audio
                return getSelectionArgsForVideoOrAudioMediaCondition(durationCondition, queryMimeCondition);
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (chooseMode) {
            case PictureConfig.TYPE_ALL:
                // Get All
                return getSelectionArgsForAllMediaType();
            case PictureConfig.TYPE_IMAGE:
                // Get Image
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            case PictureConfig.TYPE_VIDEO:
                // Get Video
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case PictureConfig.TYPE_AUDIO:
                // Get Audio
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
        }
        return null;
    }

    /**
     * Sort by the number of files
     *
     * @param imageFolders
     */
    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        Collections.sort(imageFolders, (lhs, rhs) -> {
            if (lhs.getData() == null || rhs.getData() == null) {
                return 0;
            }
            int lSize = lhs.getImageNum();
            int rSize = rhs.getImageNum();
            return Integer.compare(rSize, lSize);
        });
    }

    /**
     * Create folder
     *
     * @param firstPath
     * @param firstMimeType
     * @param imageFolders
     * @param folderName
     * @return
     */
    private LocalMediaFolder getImageFolder(String firstPath,String firstMimeType, String folderName,long folderId, List<LocalMediaFolder> imageFolders) {
//            for (LocalMediaFolder folder : imageFolders) {
//                // Under the same folder, return yourself, otherwise create a new folder
//                String name = folder.getName();
//                if (TextUtils.isEmpty(name)) {
//                    continue;
//                }
//                if (name.equals(folderName)) {
//                    return folder;
//                }
//            }
        for (LocalMediaFolder folder : imageFolders) {
            // Under the same folder, return yourself, otherwise create a new folder
            long id = folder.getBucketId();

            if (id==folderId) {
                return folder;
            }
        }
            LocalMediaFolder newFolder = new LocalMediaFolder();
            newFolder.setName(folderName);
            newFolder.setFirstImagePath(firstPath);
            newFolder.setFirstMimeType(firstMimeType);
            imageFolders.add(newFolder);
            return newFolder;

    }

    /**
     * Get video (maximum or minimum time)
     *
     * @return
     */
    private String getDurationCondition() {
        long maxS =videoMaxSecond == 0 ? Long.MAX_VALUE : videoMaxSecond;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.DURATION + " and " + MediaStore.MediaColumns.DURATION + " <= %d",
                Math.max((long) 0, videoMinSecond),
                Math.max((long) 0, videoMinSecond) == 0 ? "" : "=",
                maxS);
    }

    /**
     * Get media size (maxFileSize or miniFileSize)
     *
     * @return
     */
    private String getFileSizeCondition() {
        long maxS = filterMaxFileSize == 0 ? Long.MAX_VALUE :filterMaxFileSize;
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
                Math.max(0, filterMinFileSize),
                Math.max(0, filterMinFileSize) == 0 ? "" : "=",
                maxS);
    }

    private String getQueryMimeCondition() {
        HashSet<String> stringHashSet = null;
        if (stringHashSet == null) {
            stringHashSet = new HashSet<>();
        }
//        if (!TextUtils.isEmpty(config.specifiedFormat)) {
//            stringHashSet.add(config.specifiedFormat);
//        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<String> iterator = stringHashSet.iterator();
        int index = -1;
        while (iterator.hasNext()) {
            String value = iterator.next();
            if (TextUtils.isEmpty(value)) {
                continue;
            }
            if (chooseMode == PictureMimeType.ofVideo()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)) {
                    continue;
                }
            } else if (chooseMode == PictureMimeType.ofImage()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)) {
                    continue;
                }
            } else if (chooseMode == PictureMimeType.ofAudio()) {
                if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) || value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)) {
                    continue;
                }
            }
            index++;
            stringBuilder.append(index == 0 ? " AND " : " OR ").append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value).append("'");
        }
        if (chooseMode != PictureMimeType.ofVideo()) {
            if (!isGif && !stringHashSet.contains(PictureMimeType.ofGIF())) {
                stringBuilder.append(NOT_GIF);
            }
        }
        return stringBuilder.toString();
    }

}
