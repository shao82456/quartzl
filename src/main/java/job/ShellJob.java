package job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import util.StringUtils;

public class ShellJob implements Job {
    private Logger logger = LoggerFactory.getLogger(ShellJob.class);
    public  String scriptname;

    public void setScriptname(String scriptname) {
        this.scriptname = scriptname;
    }

    /**
     * 核心方法,Quartz Job真正的执行逻辑.
     * @param executorContext executorContext JobExecutionContext中封装有Quartz运行所需要的所有信息
     * @throws JobExecutionException execute()方法只允许抛出JobExecutionException异常
     */
    @Override
    public void execute(JobExecutionContext executorContext) throws JobExecutionException {
        //JobDetail中的JobDataMap是共用的,从getMergedJobDataMap获取的JobDataMap是全新的对象
        JobDataMap map = executorContext.getMergedJobDataMap();
        logger.info("Running ShellJob name : {} ", map.getString("name"));
        logger.info("Running ShellJob script : {} ", scriptname);
        long startTime = System.currentTimeMillis();
        logger.info("startTime: "+new Date(startTime));

        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> commands = new ArrayList<>();
        commands.add("bash");
        commands.add(scriptname);

        processBuilder.command(commands);
        logger.info("Running Job details as follows >>>>>>>>>>>>>>>>>>>>: ");
        logger.info("Running Job commands : {}  ", StringUtils.getListString(commands));
        try {
            Process process = processBuilder.start();
            logProcess(process.getInputStream(), process.getErrorStream());
        } catch (IOException e) {
            throw new JobExecutionException(e);
        }
        long endTime = System.currentTimeMillis();
        logger.info("endTime: "+new Date(endTime));

        logger.info(">>>>>>>>>>>>> Running Job has been completed , cost time :  " + (endTime - startTime)/1000 + "ms\n");
    }

    //记录Job执行内容
    private void logProcess(InputStream inputStream, InputStream errorStream) throws IOException {
        String inputLine;
        String errorLine;
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        while ((inputLine = inputReader.readLine()) != null) logger.info(inputLine);
        while ((errorLine = errorReader.readLine()) != null) logger.error(errorLine);
    }

}


