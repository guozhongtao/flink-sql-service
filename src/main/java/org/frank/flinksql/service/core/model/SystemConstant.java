package org.frank.flinksql.service.core.model;

import java.util.regex.Pattern;

public class SystemConstant {

    public final static String COMMENT_SYMBOL = "--";

    public final static String SEMICOLON = ";";

    public final static String LINE_FEED = "\n";

    public final static String SPACE = "";

    public final static long DEFALUT_CHECKPOINT_TIMEOUT= 60000;

    public final static long DEFALUT_CHECKPOINT_INTERVAL = 60000;

    public final static int DEFALUT_TOLERABLE_CHECKPOINT_FAILURE_NUMBER= 3;

    public final static String VIRGULE = "/";

    public static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

    public static final String QUERY_JOBID_KEY_WORD = "job-submitted-success:";

    public static final String QUERY_JOBID_KEY_WORD_BACKUP = "Job has been submitted with JobID";



}
