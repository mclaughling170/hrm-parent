package cn.itsource.hrm.service.impl;

import cn.itsource.hrm.domain.CourseType;
import cn.itsource.hrm.mapper.CourseTypeMapper;
import cn.itsource.hrm.service.ICourseTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程目录 服务实现类
 * </p>
 *
 * @author solargen
 * @since 2019-12-25
 */
@Service
public class CourseTypeServiceImpl extends ServiceImpl<CourseTypeMapper, CourseType> implements ICourseTypeService {

    @Override
    public List<CourseType> loadTreeData() {
        /*List<CourseType> courseTypes = getByParentId(0L);*/
        List<CourseType> courseTypes = loadTreeDataLoop();
        return courseTypes;
    }

    /**
     * 循环方式 - 二
     * @return
     */
    public List<CourseType> loadTreeDataLoop(){
        //初始化一个集合存放一级类型
        List<CourseType> firstLevelTypes = new ArrayList<>();
        //查询数据库中的所有类型
        List<CourseType> courseTypes = baseMapper.selectList(null);

        //创建一个Map，将courseTypes数据存到map中，key使用id，value就是CourseType
        Map<Long,CourseType> map = new HashMap<>();
        for (CourseType courseType : courseTypes) {
            map.put(courseType.getId(), courseType);
        }

        //循环courseTypes，分配一级类型和非一级类型
        for (CourseType courseType : courseTypes) {
            if(courseType.getPid().longValue() == 0L){
                firstLevelTypes.add(courseType);
            }else{
                CourseType parent = map.get(courseType.getPid());
                if(parent!=null){
                    parent.getChildren().add(courseType);
                }
            }
        }

        return firstLevelTypes;
    }

    /**
     * 循环方式
     * @return
     */
    public List<CourseType> loadTreeDataLoop_1(){

        //初始化一个集合存放一级类型
        List<CourseType> firstLevelTypes = new ArrayList<>();
        //查询数据库中的所有类型
        List<CourseType> courseTypes = baseMapper.selectList(null);

        for (CourseType courseType : courseTypes) {
            //如果是一级类型，直接放入到firstLevelTypes
            if(courseType.getPid()==0L){
                firstLevelTypes.add(courseType);
            }else{
                //如果不是，找父类型，放入父类型的children集合中
                for (CourseType parent : courseTypes) {
                    if(courseType.getPid().longValue() == parent.getId().longValue()){
                        parent.getChildren().add(courseType);
                    }
                }
            }
        }
        return firstLevelTypes;
    }


    /**
     * 根据父id递归查询课程类型
     * @param pid
     * @return
     */
    public List<CourseType> getByParentId(Long pid){

        List<CourseType> children = baseMapper.selectList(
                new QueryWrapper<CourseType>()
                        .eq("pid", pid)
        );
        //递归的出口
        if(children==null||children.size()==0){
            return null;
        }
        for (CourseType child : children) {
            List<CourseType> childs = getByParentId(child.getId());
            child.setChildren(childs);
        }
        return children;

    }



}
