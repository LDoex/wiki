package com.oyyk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.oyyk.wiki.domain.Content;
import com.oyyk.wiki.domain.Doc;
import com.oyyk.wiki.domain.DocExample;
import com.oyyk.wiki.exception.BusinessException;
import com.oyyk.wiki.exception.BusinessExceptionCode;
import com.oyyk.wiki.mapper.ContentMapper;
import com.oyyk.wiki.mapper.DocMapper;
import com.oyyk.wiki.mapper.DocMapperCust;
import com.oyyk.wiki.req.DocQueryReq;
import com.oyyk.wiki.req.DocSaveReq;
import com.oyyk.wiki.resp.DocQueryResp;
import com.oyyk.wiki.resp.PageResp;
import com.oyyk.wiki.util.CopyUtil;
import com.oyyk.wiki.util.RedisUtil;
import com.oyyk.wiki.util.RequestContext;
import com.oyyk.wiki.util.SnowFlake;
import com.oyyk.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private WsService wsService;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private DocMapper docMapper;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    public PageResp<DocQueryResp> list(DocQueryReq req) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DocQueryResp> respList = new ArrayList<>();
//        for(Doc doc: docList){
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
        //对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public List<DocQueryResp> all(Long ebookId) {

        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> home(DocQueryReq req) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        //动态sql，name不为空才进行模糊匹配

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<DocQueryResp> respList = new ArrayList<>();
//        for(Doc doc: docList){
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
        //对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//        }

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if(ObjectUtils.isEmpty(req.getId())){
            //新增内容到doc表
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            //新增内容到content表
            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            //更新
            docMapper.updateByPrimaryKey(doc);

            //更新content,要用带大字段的方法
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count == 0){
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public String findContent(Long id){
       Content content = contentMapper.selectByPrimaryKey(id);
       //文档阅读数+1
       docMapperCust.increaseViewCount(id);
       if(ObjectUtils.isEmpty(content)){
           return "";
       } else {
           return content.getContent();
       }
    }

    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);

    }

    /**
     * 点赞
     * @param id
     */
    public void vote(Long id){
        //文档阅读数+1
        String ip = RequestContext.getRemoteAddr();
        if(redisUtil.validateRepeat("DOC_VOTE" + id + "_" + ip, 3600*24)){
            docMapperCust.increaseVoteCount(id);
        } else{
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        //推送消息
        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        wsService.sendInfo('【'+docDb.getName()+ "】被点赞");

    }


    public void updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    }

}
