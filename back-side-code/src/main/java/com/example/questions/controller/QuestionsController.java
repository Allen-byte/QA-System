package com.example.questions.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.Result;
import com.example.questions.entity.*;
import com.example.questions.service.AnswersService;
import com.example.questions.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping("/questions")
public class QuestionsController {
    @Autowired
    HttpServletRequest request;       //请求对象，用于输出请求内容

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private AnswersService answersService;

    //查询所有问题
    @GetMapping("/all")
    public Result showQuestions(@RequestParam HashMap<String, String> params){
        Page<QuestionShow> page = new Page<>();
        page.setSize(Integer.parseInt(params.get("pageSize")));
        page.setCurrent(Integer.parseInt(params.get("pageNum")));
        QueryWrapper<QuestionShow> query = new QueryWrapper<>();
        String content = params.get("content");
        String date = params.get("date");
        String username = params.get("username");
        if(StringUtils.isNotBlank(content)){
            query.like("t1.title", content);
        }
        if(StringUtils.isNotBlank(date)){
            query.eq("t1.pose_date", date);
        }
        if(StringUtils.isNotBlank(username)){
            query.like("t3.username", username);
        }
        IPage<QuestionShow> iPage = questionsService.queryInfo(page, query);
        return iPage.getTotal()>0?Result.success(iPage.getTotal(), iPage.getRecords()):Result.fail("暂无数据");

    }

    //保存提交的问题
    @PostMapping("/add")
    public Result add(@RequestBody Questions questions){
        return questionsService.save(questions)?Result.success(0, null):Result.fail("问题保存失败");
    }

    // 删除问题
    @GetMapping("/del/{id}")
    public Result del(@PathVariable String id){
        //删除该问题下的全部回答
        LambdaQueryWrapper<Answers> wrapper = new LambdaQueryWrapper<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("qid", id);
        if(answersService.removeByMap(map)){
            System.out.println("删除回答成功");
        }else{
            System.out.println("删除回答失败");
        }
        return questionsService.removeById(id)?Result.success(0, null):Result.fail("删除问题失败");
    }


    //根据uid获取问题,用于展示提问信息
    @PostMapping("/getQuestionsInfo")
    public Result getQuestionsInfo(@RequestBody HashMap<String, String> data){
        Integer uid = Integer.valueOf(data.get("uid"));
        Integer limit = 10;            //查询最新的limit条记录
        List<recordsQ> list = questionsService.getRecords(limit, uid);
        HashMap<String, Object> hashMap = new HashMap<>();                 //返回数据
        if(list.size()>0){
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();
            String year = list.get(0).getPoseDate().substring(0, 4);    //获取日期年份
            for(recordsQ record : list){
                dates.add(record.getPoseDate().substring(5));
                counts.add(record.getCount());
            }
            int total = questionsService.lambdaQuery().eq(Questions::getUid, uid).list().size();
            hashMap.put("dates", dates);
            hashMap.put("counts", counts);
            hashMap.put("total", total);
            hashMap.put("year", year);
            return Result.success(1, hashMap);
        }else{
            return Result.fail("暂无提问");
        }
    }

    //根据uid获取用户提问
    @GetMapping("/myquestions")
    public Result myquestions(@RequestParam HashMap<String, String> hashMap){
        Page<Questions> page = new Page<Questions>();
        page.setCurrent(Integer.parseInt(hashMap.get("pageNum")));
        page.setSize(Integer.parseInt(hashMap.get("pageSize")));
        LambdaQueryWrapper<Questions> query = new LambdaQueryWrapper<>();
        query.eq(Questions::getUid, hashMap.get("uid"));
        query.orderByDesc(Questions::getPosedtime);
        IPage<Questions> iPage = questionsService.page(page, query);
        return Result.success(iPage.getTotal(), iPage.getRecords());
    }

    //更新问题
    @PostMapping("/update")
    public Result update(@RequestBody Questions questions){
        return questionsService.updateById(questions)?Result.success(0, null):Result.fail("更新失败");
    }

    //保存发布问题中的图片
    @PostMapping("/saveImage")
    public Result saveImage(Upload upload) throws FileNotFoundException {
        MultipartFile file = upload.getFile();
        if(file.isEmpty()){
            return Result.fail("上传失败");
        }
        String uid = upload.getUid();
        //避免重复，加上当前时间戳
        String fileName = uid + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        //保存的根目录路径
//        String fileDir = ResourceUtils.getURL("classpath:").getPath();
        String fileDir = System.getProperty("user.dir") + "/upload";
        //文件的保存目录路径
        File filePath = new File(fileDir + "/images");

        if(!filePath.exists()){
            System.out.println("正在创建图片文件夹：" + fileDir + "/images");
            filePath.mkdirs();
        }

        //保存的位置
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);

        //如果要保存数据库，使用这个路径
        String storePath = "/static/upload/images/" + fileName;

        HashMap<String, String> resData = new HashMap<>();
        resData.put("showUrl", storePath);       //返回给前端显式的路径
        resData.put("dest", filePath + System.getProperty("file.separator") + fileName);    //用户取消删除的路径

        try{
            file.transferTo(dest);
            return Result.success(1, resData);
        }catch(IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }
    }

    //删除回答或问题中的图片（用户取消）
    @PostMapping("/delImages")
    public Result delImages(@RequestBody HashMap<String, ArrayList<String>> hashMap){
        boolean flag = false;
        ArrayList<String> paths = hashMap.get("imagePaths");
        for(String path : paths){
            File file = new File(path);
            flag = file.delete();
        }
        return flag?Result.success(0, null):Result.fail("删除失败");
    }
}
