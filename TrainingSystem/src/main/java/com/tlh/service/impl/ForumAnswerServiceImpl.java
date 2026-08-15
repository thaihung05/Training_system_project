/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tlh.service.impl;

import com.tlh.pojo.Course;
import com.tlh.pojo.ForumAnswer;
import com.tlh.pojo.ForumQuestion;
import com.tlh.pojo.User;
import com.tlh.repository.ForumAnswerRepository;
import com.tlh.repository.ForumQuestionRepository;
import com.tlh.service.ForumAnswerService;
import com.tlh.service.NotificationService;
import com.tlh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author LENOVO
 */
@Service
@Transactional
public class ForumAnswerServiceImpl implements ForumAnswerService{

    @Autowired
    private ForumAnswerRepository forumAnswerRepo;

    @Autowired
    private ForumQuestionRepository forumQuestionRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Override
    public ForumAnswer answer(long forumQuestionId, User caller, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Câu trả lời không được để trống");
        }
        if (content.trim().length() > 65535) {
            throw new IllegalArgumentException("Câu trả lời quá dài");
        }
        ForumQuestion q = this.forumQuestionRepo.getById(forumQuestionId);
        if (q == null) {
            throw new IllegalArgumentException("Không tìm thấy câu hỏi");
        }
        ForumAnswer a = new ForumAnswer();
        a.setForumQuestionId(q);
        a.setUserId(caller);
        a.setContent(content.trim());
        this.forumAnswerRepo.saveOrUpdate(a);

        if (!q.getUserId().getId().equals(caller.getId())) {
            this.notificationService.create(q.getUserId().getId(), "Câu hỏi diễn đàn đã được trả lời",
                    "Câu hỏi \"" + q.getContent() + "\" của bạn trong khoá học " + q.getCourseId().getTitle() + " đã có người trả lời",
                    "/courses/" + q.getCourseId().getId() + "?tab=forum");
        } else {
            notifyTrainersOfReply(q.getCourseId(), caller);
        }
        return a;
    }

    private void notifyTrainersOfReply(Course course, User replier) {
        String link = "/manage/courses/" + course.getId() + "/forum";
        String content = replier.getName() + " vừa trả lời tiếp trong diễn đàn khoá học " + course.getTitle();

        if (course.getChains().isEmpty() && course.getRegions().isEmpty()) {
            for (User t : this.userService.getUsersByRole("TRAINER")) {
                if (t.getIsActive() && !t.getId().equals(replier.getId())) {
                    this.notificationService.create(t.getId(), "Có phản hồi mới trong diễn đàn", content, link);
                }
            }
            return;
        }

        if (course.getCreatedBy() != null && !course.getCreatedBy().getId().equals(replier.getId())) {
            this.notificationService.create(course.getCreatedBy().getId(), "Có phản hồi mới trong diễn đàn", content, link);
        }
    }

}
