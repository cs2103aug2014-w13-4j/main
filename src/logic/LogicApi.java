package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;

import command.*;
import common.ApplicationLogger;
import common.Command;
import common.Feedback;
import common.exceptions.FileFormatNotSupportedException;
import common.exceptions.HistoryNotFoundException;
import common.exceptions.InvalidCommandUseException;
import common.exceptions.InvalidDateFormatException;
import common.exceptions.InvalidInputException;
import common.exceptions.TaskNotFoundException;
import common.exceptions.TimeIntervalOverlapException;

//@author A0114368E
/**
 * Exhibits the facade pattern. It serves as a wrapper class for the Logic
 * component, and hides the complexity of the logic component from the Main
 * component
 *
 *
 */
public class LogicApi {

    private Logic logic;
    private static LogicApi instance = null;
    private static final String INVALID_COMMAND_MESSAGE = "The command is invalid.";
    private static final String INVALID_BEFORE_AFTER_SEARCH_MESSAGE = "Before and After cannot be searched together.";
    private static final String INVALID_FROM_TO_SEARCH_MESSAGE = "Both start and end date are required.";

    /**
     * This constructor follows the singleton pattern It can only be called with
     * in the current class (LogicApi.getInstance()) This is to ensure that only
     * there is exactly one instance of LogicApi class
     *
     * @return an object instance of LogicApi class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static LogicApi getInstance() throws IOException,
            FileFormatNotSupportedException {
        if (instance == null) {
            instance = new LogicApi();
            ApplicationLogger.getLogger().log(Level.INFO,
                    "Initializing Logic API.");
            instance.logic = Logic.getInstance();
        }
        return instance;
    }

    /**
     * Always creates a new instance of the LogicApi class. For debugging
     * purposes.
     *
     * @return an object instance of the LogicApi class.
     * @throws IOException
     * @throws FileFormatNotSupportedException
     */
    public static LogicApi getNewInstance() throws IOException,
            FileFormatNotSupportedException {
        instance = new LogicApi();
        ApplicationLogger.getLogger()
                .log(Level.INFO, "Initializing Logic API.");
        instance.logic = Logic.getNewInstance();
        return instance;
    }

    private LogicApi() {
    }

    public Feedback displayAllActive() {
        return logic.displayAllActive();
    }

    /**
     * Main function to call to execute command
     *
     * @param the
     *            command created by the commandParser
     * @return the feedback (tasklist and message) corresponding to the
     *         particular command
     * @throws InvalidDateFormatException
     * @throws IOException
     * @throws TaskNotFoundException
     * @throws InvalidInputException
     * @throws HistoryNotFoundException
     * @throws InvalidCommandUseException
     * @throws TimeIntervalOverlapException
     * @throws EmptySearchResultException
     */
    public Feedback executeCommand(Command command)
            throws TaskNotFoundException, IOException,
            InvalidDateFormatException, InvalidInputException,
            HistoryNotFoundException, InvalidCommandUseException,
            TimeIntervalOverlapException {
        if (logic.storage == null) {
            throw new IOException();
        } else {
            ApplicationLogger.getLogger().log(
                    Level.INFO,
                    "Executing command: " + command.getCommand() + " "
                            + command.getParam());
            CommandEnum commandType = command.getCommand();
            Hashtable<ParamEnum, ArrayList<String>> param = command.getParam();
            assert hasKeywordParam(param);
            switch (commandType) {
            case ADD:
                assert hasNameParam(param);
                if (!isNameParamEmpty(param)) {
                    return logic.add(param);
                }
                break;
            case DELETE:
                if (!isKeywordParamEmpty(param)) {
                    return logic.delete(param);
                }
                break;
            case UPDATE:
                if (!isKeywordParamEmpty(param)) {
                    return logic.update(param);
                }
                break;
            case UNDO:
                return logic.undo();
            case DISPLAY:
                return logic.display(param);
            case CLEAR:
                return logic.clear(param);
            case DONE:
                if (!isKeywordParamEmpty(param)) {
                    return logic.complete(param);
                }
                break;
            case SEARCH:
                if (hasSearchParams(param)) {
                    return logic.search(param);
                }
                break;
            case CONFIRM:
                if (!isKeywordParamEmpty(param) && hasIdParam(param)) {
                    return logic.confirm(param);
                }
                break;
            case SUGGEST:
                if (hasStartEndDurationParams(param)) {
                    return logic.suggest(param);
                }
                break;
            case ACCEPT:
                if (!isKeywordParamEmpty(param)) {
                    return logic.accept(param);
                }
            default:
                break;
            }
            throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
        }
    }

    private boolean hasStartEndDurationParams(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.START_DATE)
                && param.containsKey(ParamEnum.END_DATE)
                && param.containsKey(ParamEnum.DURATION);
    }

    private boolean hasBothBeforeAndAfterParams(
            Hashtable<ParamEnum, ArrayList<String>> params) {
        return params.containsKey(ParamEnum.BEFORE)
                && params.containsKey(ParamEnum.AFTER);
    }

    private boolean hasIdParam(Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.ID);
    }

    private boolean hasKeywordParam(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.KEYWORD);
    }

    private boolean hasNameParam(Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.containsKey(ParamEnum.NAME);
    }

    private boolean hasOnlyFromOrToParams(
            Hashtable<ParamEnum, ArrayList<String>> params) {
        return (params.containsKey(ParamEnum.START_DATE) ^ params
                .containsKey(ParamEnum.END_DATE));
    }

    private boolean hasSearchParams(
            Hashtable<ParamEnum, ArrayList<String>> params)
            throws InvalidInputException {
        if (hasBothBeforeAndAfterParams(params)) {
            throw new InvalidInputException(INVALID_BEFORE_AFTER_SEARCH_MESSAGE);
        } else if (hasOnlyFromOrToParams(params)) {
            throw new InvalidInputException(INVALID_FROM_TO_SEARCH_MESSAGE);
        } else {
            return true;
        }
    }

    private boolean isKeywordParamEmpty(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.KEYWORD).get(0).isEmpty();
    }

    private boolean isNameParamEmpty(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.NAME).get(0).isEmpty();
    }

}
