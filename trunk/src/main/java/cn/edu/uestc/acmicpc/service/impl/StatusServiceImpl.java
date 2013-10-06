package cn.edu.uestc.acmicpc.service.impl;

import java.util.List;

import cn.edu.uestc.acmicpc.db.dto.impl.status.StatusDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.status.StatusInformationDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.status.StatusListDTO;
import cn.edu.uestc.acmicpc.db.entity.Status;
import cn.edu.uestc.acmicpc.web.view.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import cn.edu.uestc.acmicpc.db.condition.impl.StatusCondition;
import cn.edu.uestc.acmicpc.db.dao.iface.IStatusDAO;
import cn.edu.uestc.acmicpc.service.iface.StatusService;
import cn.edu.uestc.acmicpc.util.Global;
import cn.edu.uestc.acmicpc.util.exception.AppException;

/**
 * Implementation for {@link StatusService}.
 */
@Service
@Primary
public class StatusServiceImpl extends AbstractService implements StatusService {

  private final IStatusDAO statusDAO;

  @Autowired
  public StatusServiceImpl(IStatusDAO statusDAO) {
    this.statusDAO = statusDAO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> findAllUserAcceptedProblemIds(Integer userId) throws AppException {
    StatusCondition statusCondition = new StatusCondition();
    statusCondition.userId = userId;
    statusCondition.resultId = Global.OnlineJudgeReturnType.OJ_AC.ordinal();
    // TODO(mzry1992): please test for this statement.
    return (List<Integer>) statusDAO.findAll("problemByProblemId.problemId",
        statusCondition.getCondition());
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> findAllUserTriedProblemIds(Integer userId) throws AppException {
    StatusCondition statusCondition = new StatusCondition();
    statusCondition.userId = userId;
    // TODO(mzry1992): please test for this statement.
    return (List<Integer>) statusDAO.findAll("problemByProblemId.problemId",
        statusCondition.getCondition());
  }

  @Override
  public Long count(StatusCondition condition) throws AppException {
    return statusDAO.count(condition.getCondition());
  }

  @Override
  public List<StatusListDTO> getStatusList(StatusCondition condition, PageInfo pageInfo) throws AppException {
    condition.currentPage = pageInfo.getCurrentPage();
    condition.countPerPage = Global.RECORD_PER_PAGE;
    return statusDAO.findAll(StatusListDTO.class, StatusListDTO.builder(),
        condition.getCondition());
  }

  private void updateStatusByStatusDTO(Status status, StatusDTO statusDTO) {
    if (statusDTO.getResult() != null)
      status.setResult(statusDTO.getResult());
    if (statusDTO.getMemoryCost() != null)
      status.setMemoryCost(statusDTO.getMemoryCost());
    if (statusDTO.getTimeCost() != null)
      status.setTimeCost(statusDTO.getTimeCost());
    if (statusDTO.getLength() != null)
      status.setLength(statusDTO.getLength());
    if (statusDTO.getTime() != null)
      status.setTime(statusDTO.getTime());
    if (statusDTO.getCaseNumber() != null)
      status.setCaseNumber(statusDTO.getCaseNumber());
    if (statusDTO.getCodeId() != null)
      status.setCodeId(statusDTO.getCodeId());
    if (statusDTO.getCompileInfoId() != null)
      status.setCompileInfoId(statusDTO.getCompileInfoId());
    if (statusDTO.getContestId() != null)
      status.setContestId(statusDTO.getContestId());
    if (statusDTO.getLanguageId() != null)
      status.setLanguageId(statusDTO.getLanguageId());
    if (statusDTO.getProblemId() != null)
      status.setProblemId(statusDTO.getProblemId());
    if (statusDTO.getUserId() != null)
      status.setUserId(statusDTO.getUserId());
  }

  @Override
  public void createNewStatus(StatusDTO statusDTO) throws AppException {
    Status status = new Status();
    updateStatusByStatusDTO(status, statusDTO);
    statusDAO.add(status);
  }

  @Override
  public StatusInformationDTO getStatusInformation(Integer statusId) throws AppException {
    return statusDAO.getDTOByUniqueField(StatusInformationDTO.class,
        StatusInformationDTO.builder(), "statusId", statusId);
  }

  @Override
  public IStatusDAO getDAO() {
    return statusDAO;
  }
}
