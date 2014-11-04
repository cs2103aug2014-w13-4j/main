package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import models.ApplicationLogger;
import models.Command;
import models.Feedback;
import command.*;
import exceptions.HistoryNotFoundException;
import exceptions.InvalidCommandUseException;
import exceptions.InvalidDateFormatException;
import exceptions.InvalidInputException;
import exceptions.TaskNotFoundException;
import exceptions.TimeIntervalOverlapException;

//TODO: Throw exceptions when mandatory fields are missing
public class LogicApi {
    private Logic logic;
    private static final String INVALID_COMMAND_MESSAGE = "The command is invalid.";
    private static final String INVALID_BEFORE_AFTER_SEARCH_MESSAGE = "Before and After cannot be searched together.";
    private static final String INVALID_FROM_TO_SEARCH_MESSAGE = "Both start and end date are required.";

    public LogicApi() {

    }

    /**
     * constructor This constructor follows the singleton pattern It can only be
     * called with in the current class (Logic.getInstance()) This is to ensure
     * that only there is exactly one instance of Logic class
     *
     * @throws FileFormatNotSupportedException
     *             , IOException
     * @return Logic object
     *
     *         To be implemented in the future
     */
    /**
     * private static Logic instance = null;
     *
     * private Logic() {
     *
     * }
     *
     * public static Logic getInstance() { if (instance == null) { instance =
     * new Logic(); } return instance; }
     **/

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
            HistoryNotFoundException, InvalidCommandUseException, TimeIntervalOverlapException {
        if (logic.storage == null) {
            throw new IOException();
        } else {
            ApplicationLogger.getApplicationLogger().log(
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
            case DONE:
            case COMPLETE:
                if (!isKeywordParamEmpty(param)) {
                    return logic.complete(param);
                }
                break;
            case LEVEL:
                return null;
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
            case TAG:
                break;
            default:
                break;
            }
            throw new InvalidInputException(INVALID_COMMAND_MESSAGE);
        }
    }

    /**
     * Initialises the logic object by creating its corresponding storage object
     * It also catches the exceptions that can be thrown
     *
     * @return the feedback indicating whether the storage has been successfully
     *         loaded.
     */
    public Feedback initialize() {
        ApplicationLogger.getApplicationLogger().log(Level.INFO,
                "Initializing Logic API.");
        logic = new Logic();
        return logic.initialize();
    }

    private boolean hasSearchParams(
            Hashtable<ParamEnum, ArrayList<String>> params)
            throws InvalidInputException {
        List<ParamEnum> searchParams = Arrays.asList(CommandEnum.SEARCH
                .params());
        if (hasBothBeforeAndAfterParams(params)) {
            throw new InvalidInputException(INVALID_BEFORE_AFTER_SEARCH_MESSAGE);
        } else if (hasOnlyFromOrToParams(params)) {
            throw new InvalidInputException(INVALID_FROM_TO_SEARCH_MESSAGE);
        } else {
            for (ParamEnum param : params.keySet()) {
                if (searchParams.contains(param)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean hasOnlyFromOrToParams(
            Hashtable<ParamEnum, ArrayList<String>> params) {
        return (params.containsKey(ParamEnum.START_DATE) ^ params
                .containsKey(ParamEnum.END_DATE));
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

    private boolean isKeywordParamEmpty(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.KEYWORD).get(0).isEmpty();
    }

    private boolean isNameParamEmpty(
            Hashtable<ParamEnum, ArrayList<String>> param) {
        return param.get(ParamEnum.NAME).get(0).isEmpty();
    }

}
